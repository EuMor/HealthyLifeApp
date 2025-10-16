package moroshkinem.healthylife.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.compose.material.icons.Icons
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Person

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "medication_reminders"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val courseId = intent.getLongExtra("courseId", 0L)
        val name = intent.getStringExtra("name") ?: "Лекарство"

        createNotificationChannel(context)  // Создаём канал

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Время принимать $name!")  // ✅ Правильно
            .setContentText("Не забудьте сегодня!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(courseId.hashCode(), notification)
    }

    fun createNotificationChannel(context: Context) {
        val name = "Medication Reminders"
        val descriptionText = "Напоминания о приёме лекарств"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}