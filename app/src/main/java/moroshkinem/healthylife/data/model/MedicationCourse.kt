package moroshkinem.healthylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_courses")
data class MedicationCourse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",                 // ✅ Default
    val startDate: String = "",            // ✅ Default
    val durationDays: Int = 0,             // ✅ Default
    val dailyTime: String = "09:00",       // ✅ Default
    val isActive: Boolean = true,
    val endDate: String? = null            // ✅ Добавь поле
)

@Entity(
    tableName = "daily_intakes",
    primaryKeys = ["courseId", "date"]
)
data class DailyIntake(
    val courseId: Long,
    val date: String,              // "2024-01-15"
    val isTaken: Boolean = false,
    val takenTime: String? = null  // "09:05" (если принял)
)

// Для UI: прогресс курса
data class CourseProgress(
    val course: MedicationCourse,
    val completedDays: Int,
    val totalDays: Int,
    val progress: Float,
    val todayIntake: DailyIntake?  // Сегодняшняя запись
)