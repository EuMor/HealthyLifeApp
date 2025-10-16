package moroshkinem.healthylife.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.Steps

@Dao
interface StepsDao {
    @Query("SELECT * FROM steps WHERE date = :date")
    suspend fun getStepsForDate(date: String): Steps?

    @Query("SELECT * FROM steps WHERE date BETWEEN :start AND :end ORDER BY date")
    fun getStepsForWeekAsFlow(start: String, end: String): Flow<List<Steps>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: Steps)

    @Query("UPDATE steps SET count = :count WHERE date = :date")
    suspend fun updateStepsCount(date: String, count: Int)

    @Query("UPDATE steps SET baselineCount = :baseline WHERE date = :date")
    suspend fun updateBaseline(date: String, baseline: Int)
}