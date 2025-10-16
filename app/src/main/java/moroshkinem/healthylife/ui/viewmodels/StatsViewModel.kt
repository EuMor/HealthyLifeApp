package moroshkinem.healthylife.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import moroshkinem.healthylife.data.model.DailyStats

import moroshkinem.healthylife.data.repos.StepsRepository
import moroshkinem.healthylife.data.repos.WaterRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class StatsViewModel(
    private val waterRepository: WaterRepository,
    private val stepsRepository: StepsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        combine(
            waterRepository.getThisWeekFlow(),
            stepsRepository.getThisWeekFlow()
        ) { waterList, stepsList ->

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            val days = (0..6).map { offset ->
                val date = LocalDate.now().minusDays(6 - offset.toLong())
                val dateStr = date.format(formatter)
                val water = waterList.find { it.date == dateStr }?.amountInMl ?: 0
                val steps = stepsList.find { it.date == dateStr }?.count ?: 0

                DailyStats(
                    day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                    water = water,
                    steps = steps
                )
            }

            val totalWater = days.sumOf { it.water }
            val totalSteps = days.sumOf { it.steps }
            val averageWater = if (days.isNotEmpty()) totalWater / days.size else 0
            val averageSteps = if (days.isNotEmpty()) totalSteps / days.size else 0

            StatsUiState(
                weeklyData = days,
                totalWater = totalWater,
                totalSteps = totalSteps,
                averageWater = averageWater,
                averageSteps = averageSteps
            )
        }
            .onEach { stats ->
                _uiState.value = stats
            }
            .onStart { _uiState.value = StatsUiState(weeklyData = emptyList()) }
            .catch { e ->
                // лог или fallback
                _uiState.value = StatsUiState()
            }
            .launchIn(viewModelScope)
    }

    data class StatsUiState(
        val weeklyData: List<DailyStats> = emptyList(),
        val totalWater: Int = 0,
        val totalSteps: Int = 0,
        val averageWater: Int = 0,
        val averageSteps: Int = 0
    )

//    data class DailyStats(
//        val day: String,      // Например, "Mon"
//        val water: Int,       // миллилитров воды
//        val steps: Int        // количество шагов
//    )
//
//    data class DailyWater(
//        val day: String,
//        val amount: Int
//    )
//    data class DailySteps(
//        val day: String,
//        val steps: Int
//    )
}