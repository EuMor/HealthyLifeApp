package moroshkinem.healthylife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.Sleep
import moroshkinem.healthylife.data.model.SleepSession

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SleepSession)  // ✅ OK

    // Old Sleep
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: Sleep)  // ✅ Keep for backward

    @Query("SELECT * FROM sleep WHERE date = :date")
    fun getSleepForDate(date: String): Flow<Sleep?>

    // New SleepSession
    @Query("SELECT * FROM sleep_sessions WHERE date = :date LIMIT 1")
    fun getTodaySessionFlow(date: String): Flow<SleepSession?>

    @Query("SELECT AVG(durationHours) FROM sleep_sessions WHERE date BETWEEN :start AND :end")
    suspend fun getAverageSleep(start: String, end: String): Float?
}