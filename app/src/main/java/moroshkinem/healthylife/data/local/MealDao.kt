package moroshkinem.healthylife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.Meal

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT SUM(calories) FROM meals WHERE date = :date")
    fun getCaloriesForDateFlow(date: String): Flow<Int?>

    @Query("SELECT * FROM meals WHERE date BETWEEN :start AND :end ORDER BY date")
    fun getMealsForWeek(start: String, end: String): Flow<List<Meal>>
}