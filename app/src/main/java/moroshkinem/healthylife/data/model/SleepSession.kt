package moroshkinem.healthylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,  // YYYY-MM-DD
    val startTime: String,  // HH:MM
    val endTime: String,
    val durationHours: Float,
    val qualityScore: Int = 0,  // 0-100
    val stages: String? = null  // JSON: {"deep":2h, "light":5h}
)