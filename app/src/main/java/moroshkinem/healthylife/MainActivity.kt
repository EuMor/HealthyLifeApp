package moroshkinem.healthylife


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import moroshkinem.healthylife.data.database.AppDatabase
import moroshkinem.healthylife.data.model.MedicationCourse
import moroshkinem.healthylife.util.ReminderReceiver
import moroshkinem.healthylife.workers.StepReminderWorker
import moroshkinem.healthylife.workers.WaterReminderWorker
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    val Context.appDatabase: AppDatabase
        get() = Room.databaseBuilder(this, AppDatabase::class.java, "healthy_life_db").build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleWaterReminders()
        scheduleStepReminder()
        ReminderReceiver().createNotificationChannel(this)  // Инициализация канала
        setContent {
            HealthyLifeAppUI()
        }
    }
    private fun scheduleWaterReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            1, TimeUnit.DAYS // Каждый день
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("water_reminder", ExistingPeriodicWorkPolicy.KEEP, repeatingRequest)
    }

    private fun scheduleStepReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<StepReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(20, TimeUnit.HOURS) // в 20:00
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("step_reminder", ExistingPeriodicWorkPolicy.KEEP, request)
    }

    private fun scheduleDailyReminders(course: MedicationCourse) {
        // AlarmManager для ежедневного пинга
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = LocalTime.parse(course.dailyTime)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("courseId", course.id)
            putExtra("name", course.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, course.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
