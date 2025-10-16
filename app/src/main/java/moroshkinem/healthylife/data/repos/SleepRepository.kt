package moroshkinem.healthylife.data.repos


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import moroshkinem.healthylife.data.local.SleepDao
import moroshkinem.healthylife.data.model.Sleep
import java.time.LocalDate

class SleepRepository(private val sleepDao: SleepDao) {
    suspend fun saveSleep(hours: Float) {
        val today = LocalDate.now().toString()
        sleepDao.insertSleep(Sleep(today, hours))
    }

    fun getTodaySleepFlow(): Flow<Float> = sleepDao.getSleepForDate(LocalDate.now().toString()).map { it?.durationHours ?: 0f }
}