package moroshkinem.healthylife.data.repos

import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.local.StepsDao
import moroshkinem.healthylife.data.model.Steps
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepsRepository(private val stepsDao: StepsDao) {

    companion object {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    fun getThisWeekFlow(): Flow<List<Steps>> {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(6)
        return stepsDao.getStepsForWeekAsFlow(
            start = weekAgo.format(formatter),
            end = today.format(formatter)
        )
    }

    /**
     * Сохраняет шаги за сегодня
     * @param totalStepsSinceBoot - общее количество шагов с момента загрузки устройства
     */
    suspend fun saveTodaySteps(totalStepsSinceBoot: Int) {
        val today = LocalDate.now().format(formatter)
        val existing = stepsDao.getStepsForDate(today)

        if (existing == null) {
            // Первая запись за день - устанавливаем baseline
            stepsDao.insertSteps(
                Steps(
                    date = today,
                    count = 0,
                    baselineCount = totalStepsSinceBoot
                )
            )
        } else {
            // Обновляем количество шагов за день
            val stepsToday = (totalStepsSinceBoot - existing.baselineCount).coerceAtLeast(0)
            stepsDao.updateStepsCount(today, stepsToday)
        }
    }

    /**
     * Получает количество шагов за сегодня
     */
    suspend fun getTodaySteps(): Int {
        val today = LocalDate.now().format(formatter)
        return stepsDao.getStepsForDate(today)?.count ?: 0
    }

    /**
     * Сбрасывает шаги на сегодня
     */
    suspend fun clearTodaySteps() {
        val today = LocalDate.now().format(formatter)
        stepsDao.insertSteps(Steps(today, 0, 0))
    }

    /**
     * Обновляет baseline для нового дня
     * Вызывается при смене даты
     */
    suspend fun resetBaselineForNewDay(currentTotalSteps: Int) {
        val today = LocalDate.now().format(formatter)
        stepsDao.insertSteps(
            Steps(
                date = today,
                count = 0,
                baselineCount = currentTotalSteps
            )
        )
    }
}