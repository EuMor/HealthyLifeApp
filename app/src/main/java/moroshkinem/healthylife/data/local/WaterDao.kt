package moroshkinem.healthylife.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.Water
import java.time.LocalDate

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_intake WHERE date = :date")
    fun getWaterForDateAsFlow(date: String): Flow<Water?>

    @Query("SELECT * FROM water_intake WHERE date = :date")
    suspend fun getWaterForDate(date: String): Water?

    @Query("SELECT * FROM water_intake WHERE date BETWEEN :start AND :end ORDER BY date")
    suspend fun getWaterForWeek(start: String, end: String): List<Water>

    @Query("SELECT * FROM water_intake WHERE date BETWEEN :start AND :end ORDER BY date")
    fun getWaterForWeekAsFlow(start: String, end: String): Flow<List<Water>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: Water)
}