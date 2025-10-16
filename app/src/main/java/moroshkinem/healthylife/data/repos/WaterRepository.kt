package moroshkinem.healthylife.data.repos

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import moroshkinem.healthylife.data.local.WaterDao
import moroshkinem.healthylife.data.model.Water
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaterRepository(private val waterDao: WaterDao) {


    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    fun getThisWeekFlow(): Flow<List<Water>> {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(6)
        return waterDao.getWaterForWeekAsFlow(
            start = weekAgo.format(formatter),
            end = today.format(formatter)
        )
    }

    suspend fun getThisWeek(): List<Water> {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(6)
        return waterDao.getWaterForWeek(
            start = weekAgo.format(formatter),
            end = today.format(formatter)
        )
    }
    fun getWaterForDateAsFlow(date: String):Flow<Water?>  {
        return waterDao.getWaterForDateAsFlow(date)
           // .map { it?.amountInMl ?: 0 } // ← it — это Water?, поэтому it?.amountInMl
    }

    suspend fun getTodayWater(): Water? {
        val today = java.time.LocalDate.now().toString()
        return waterDao.getWaterForDate(today)
    }

    suspend fun addWater(amountInMl: Int) {
        val today = java.time.LocalDate.now().toString()
        val existing = waterDao.getWaterForDate(today)
        val newAmount = (existing?.amountInMl ?: 0) + amountInMl
        waterDao.insertWater(Water(today, newAmount))
    }
    suspend fun clearTodayWater() {
        val today = java.time.LocalDate.now().toString()
        // В Room нет прямого DELETE, только DELETE по сущности.
        // Проще всего: вставить 0.
        waterDao.insertWater(Water(today, 0))
    }
}