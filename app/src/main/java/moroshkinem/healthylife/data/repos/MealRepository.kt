package moroshkinem.healthylife.data.repos

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import moroshkinem.healthylife.data.local.MealDao
import moroshkinem.healthylife.data.model.Meal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MealRepository(private val mealDao: MealDao) {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayCaloriesFlow(): Flow<Int> {
        return mealDao.getCaloriesForDateFlow(LocalDate.now().format(formatter))
            .map { it ?: 0 }
    }

    suspend fun addMeal(calories: Int, description: String?) {
        val now = LocalDateTime.now()
        mealDao.insertMeal(
            Meal(
                date = now.format(formatter),
                time = now.format(DateTimeFormatter.ofPattern("HH:mm")),
                calories = calories,
                description = description
            )
        )
    }

    // Метод для статистики
    fun getThisWeekMealsFlow(): Flow<List<Meal>> {
        val today = LocalDate.now().format(formatter)
        val weekAgo = LocalDate.now().minusDays(6).format(formatter)
        return mealDao.getMealsForWeek(weekAgo, today)
    }
}