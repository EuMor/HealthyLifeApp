package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.model.Meal
import moroshkinem.healthylife.data.repos.MealRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MealViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    // 🔹 Калории за сегодняшний день
    val todayCalories: StateFlow<Int> =
        mealRepository.getTodayCaloriesFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    // 🔹 Список приёмов пищи за неделю (для графиков/списка)
    val weeklyMeals: StateFlow<List<Meal>> =
        mealRepository.getThisWeekMealsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 🔹 Добавление нового приёма пищи
    fun addMeal(calories: Int, description: String?) {
        viewModelScope.launch {
            mealRepository.addMeal(calories, description)
        }
    }
}