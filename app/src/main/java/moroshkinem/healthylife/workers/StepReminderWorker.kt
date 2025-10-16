package moroshkinem.healthylife.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import moroshkinem.healthylife.R
import moroshkinem.healthylife.data.dataStore
import moroshkinem.healthylife.data.database.AppDatabase
import androidx.datastore.preferences.core.intPreferencesKey
import java.time.LocalDate

class StepReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    // 🚀 Главный метод: вызывается системой
    override suspend fun doWork(): Result {
        val steps = getTodayStepsFromDb()
        val goal = getStepGoalFromDataStore()

        if (steps < goal * 0.8) { // Если не достигнуто 80% цели
            showNotification("Ты почти достиг цели! Осталось ${goal - steps} шагов.")
        }

        return Result.success()
    }

    // 📦 Получаем шаги за сегодня из БД
    private suspend fun getTodayStepsFromDb(): Int {
        val db = AppDatabase.getDatabase(applicationContext)
        val today = LocalDate.now().toString()
        return db.stepsDao().getStepsForDate(today)?.count ?: 0
    }

    // 📦 Получаем цель по шагам из DataStore
    private suspend fun getStepGoalFromDataStore(): Int {
        val dataStore = applicationContext.dataStore
        val STEP_GOAL = intPreferencesKey("daily_step_goal")
        return dataStore.data.first()[STEP_GOAL] ?: 10000
    }

    // 🔔 Уведомление
    private fun showNotification(message: String) {
        val channelId = "step_reminder_channel"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Шаги - напоминания",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // или свой иконкой
            .setContentTitle("Напоминание о шагах")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}