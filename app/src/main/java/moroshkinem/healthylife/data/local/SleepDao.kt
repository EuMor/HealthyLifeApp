package moroshkinem.healthylife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.Sleep

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: Sleep) // ✅ Соответствует saveSleep

    @Query("SELECT * FROM sleep WHERE date = :date")
    fun getSleepForDate(date: String): Flow<Sleep?> // ✅ Соответствует getTodaySleepFlow
}