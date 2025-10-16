package moroshkinem.healthylife.data.repos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import moroshkinem.healthylife.data.local.SleepDao
import moroshkinem.healthylife.data.model.Sleep
import moroshkinem.healthylife.data.model.SleepSession
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min

class SleepRepository(
    private val sleepDao: SleepDao,
    private val context: Context
) {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val zoneId = ZoneId.systemDefault()
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    fun getTodaySleepFlow(): Flow<Float> =
        sleepDao.getTodaySessionFlow(LocalDate.now().toString())
            .map { it?.durationHours ?: 0f }

    suspend fun saveSleep(hours: Float) {
        val today = LocalDate.now().toString()
        sleepDao.insertSleep(Sleep(today, hours))
    }

    suspend fun syncFromHealthConnect() {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(zoneId).toInstant()
        val endOfDay = today.plusDays(1).atStartOfDay(zoneId).toInstant()

        val request = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
        )

        try {
            val response = healthConnectClient.readRecords(request)
            response.records.forEach { record ->
                val durationSeconds =
                    Duration.between(record.startTime, record.endTime).seconds
                val session = SleepSession(
                    date = today.toString(),
                    startTime = record.startTime.atZone(zoneId).format(formatter),
                    endTime = record.endTime.atZone(zoneId).format(formatter),
                    durationHours = durationSeconds / 3600f,
                    qualityScore = calculateScore(durationSeconds.toFloat() / 3600f)
                )
                sleepDao.insertSession(session)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateScore(hours: Float): Int {
        return min(100, (hours / 8f * 100).toInt())
    }

    suspend fun startBedtimeTracking() {
        val now = Instant.now()
        val offset = zoneId.rules.getOffset(now)

        val record = SleepSessionRecord(
            startTime = now,
            endTime = now.plusSeconds(3600),  // Обычно null, но в Health Connect это обязательный параметр
            startZoneOffset = offset,
            endZoneOffset = offset,
            title = "Сон начат вручную",
            notes = null
        )

        healthConnectClient.insertRecords(listOf(record))
    }

    suspend fun endBedtimeTracking() {
        val now = Instant.now()
        val offset = zoneId.rules.getOffset(now)

        val record = SleepSessionRecord(
            startTime = now.minusSeconds(3600),
            endTime = now,
            startZoneOffset = offset,
            endZoneOffset = offset,
            title = "Сон завершен вручную",
            notes = null
        )

        healthConnectClient.insertRecords(listOf(record))
    }



    suspend fun sendNotification(title: String) {
        val channelId = "sleep_reminders"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sleep Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText("Время сна!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}