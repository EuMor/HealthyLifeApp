package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.model.CourseProgress
import moroshkinem.healthylife.data.model.UserProfile
import moroshkinem.healthylife.data.model.Water
import moroshkinem.healthylife.data.repos.StepsRepository
import moroshkinem.healthylife.data.repos.WaterRepository
import moroshkinem.healthylife.data.sensors.StepCounterManager
import moroshkinem.healthylife.data.repos.MealRepository
import moroshkinem.healthylife.data.repos.MedicationRepository
import moroshkinem.healthylife.data.repos.SleepRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val waterRepository: WaterRepository,
    private val dataStoreManager: DataStoreManager,
    private val stepCounterManager: StepCounterManager,
    private val stepsRepository: StepsRepository,
    private val mealRepository: MealRepository,
    private val sleepRepository: SleepRepository,
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // ✅ Обработанные flows (без nulls)
    private val todayWaterFlow: Flow<Water> = waterRepository
        .getWaterForDateAsFlow(LocalDate.now().toString())
        .map { it ?: Water(LocalDate.now().toString(), 0) }

    private val todayCaloriesFlow: Flow<Int> = mealRepository.getTodayCaloriesFlow()
        .map { it ?: 0 }

    private val todaySleepFlow: Flow<Float> = sleepRepository.getTodaySleepFlow()
        .map { it ?: 0f }

    // Шаги
    val steps: StateFlow<Int> = stepCounterManager.todaySteps

    // Профиль
    val profile: StateFlow<UserProfile> = dataStoreManager.profileFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserProfile()
    )

    // Калории сожжённые
    val caloriesBurned: StateFlow<Int> = combine(steps, profile) { steps, profile ->
        (steps * (0.03f + profile.weight / 1000f)).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Калории потреблённые
    val caloriesConsumed: StateFlow<Int> = todayCaloriesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Прогресс калорий
    val calorieProgress: StateFlow<Float> = combine(caloriesConsumed, profile) { consumed, profile ->
        val goal = profile.dailyCalorieGoal.toFloat().coerceAtLeast(1f)
        (consumed / goal).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Сон
    val sleepProgress: StateFlow<Float> = combine(todaySleepFlow, profile) { hours, profile ->
        val goal = profile.dailySleepGoal.coerceAtLeast(0.1f)
        (hours / goal).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // ✅ Медикаменты
    val medicationProgresses: StateFlow<List<CourseProgress>> = medicationRepository
        .getActiveCoursesProgressFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadWaterAndGoal()
        stepCounterManager.start()
    }

    private fun loadWaterAndGoal() {
        // Создаем отдельный Combine для water, calories, sleep
        val waterProgressFlow = combine(
            todayWaterFlow,
            dataStoreManager.waterGoalFlow
        ) { water, waterGoal ->
            val waterAmount = water.amountInMl
            val waterProgress = (waterAmount.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f)
            WaterProgress(waterAmount, waterGoal, waterProgress)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WaterProgress(0, 0, 0f))

        // Создаем отдельный Combine для calories
        val calorieProgressFlow = combine(
            todayCaloriesFlow,
            profile
        ) { consumed, profile ->
            val calorieGoal = profile.dailyCalorieGoal
            val calorieProgress = (consumed.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f)
            CalorieProgress(consumed, calorieGoal, calorieProgress)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalorieProgress(0, 0, 0f))

        // Создаем отдельный Combine для sleep
        val sleepProgressFlow = combine(
            todaySleepFlow,
            profile
        ) { sleepHours, profile ->
            val sleepGoal = profile.dailySleepGoal
            val sleepProgress = (sleepHours / sleepGoal).coerceIn(0f, 1f)
            SleepProgress(sleepHours, sleepGoal, sleepProgress)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SleepProgress(0f, 0f, 0f))

        // Теперь объединяем все вместе
        combine(
            waterProgressFlow,
            calorieProgressFlow,
            sleepProgressFlow,
            medicationProgresses
        ) { waterProgress, calorieProgress, sleepProgress, medsProgress ->
            HomeUiState(
                waterAmount = waterProgress.waterAmount,
                waterGoal = waterProgress.waterGoal,
                waterProgress = waterProgress.waterProgress,
                caloriesConsumed = calorieProgress.consumed,
                calorieGoal = calorieProgress.calorieGoal,
                calorieProgress = calorieProgress.calorieProgress,
                sleepHours = sleepProgress.sleepHours,
                sleepGoal = sleepProgress.sleepGoal,
                sleepProgress = sleepProgress.sleepProgress,
                medicationProgresses = medsProgress
            )
        }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    // Добавляем data classes для промежуточных значений
    data class WaterProgress(val waterAmount: Int = 0, val waterGoal: Int = 0, val waterProgress: Float = 0f)
    data class CalorieProgress(val consumed: Int = 0, val calorieGoal: Int = 0, val calorieProgress: Float = 0f)
    data class SleepProgress(val sleepHours: Float = 0f, val sleepGoal: Float = 0f, val sleepProgress: Float = 0f)

    fun markMedicationToday(courseId: Long) {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            medicationRepository.markAsTaken(courseId, today)
        }
    }

    fun resetTodayStats() {
        viewModelScope.launch {
            waterRepository.clearTodayWater()
            stepsRepository.clearTodaySteps()
            stepCounterManager.resetStepsStartValue()
            // Добавь clear для meal, sleep, medication
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stop()
    }

    data class HomeUiState(
        val waterAmount: Int = 0,
        val waterGoal: Int = 2000,
        val waterProgress: Float = 0f,
        val caloriesConsumed: Int = 0,
        val calorieGoal: Int = 2000,
        val calorieProgress: Float = 0f,
        val sleepHours: Float = 0f,
        val sleepGoal: Float = 8f,
        val sleepProgress: Float = 0f,
        val medicationProgresses: List<CourseProgress> = emptyList()
    )
}