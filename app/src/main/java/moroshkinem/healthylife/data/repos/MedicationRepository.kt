package moroshkinem.healthylife.data.repos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import moroshkinem.healthylife.data.local.MedicationDao
import moroshkinem.healthylife.data.model.CourseProgress
import moroshkinem.healthylife.data.model.DailyIntake
import moroshkinem.healthylife.data.model.MedicationCourse
import moroshkinem.healthylife.util.ReminderReceiver
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MedicationRepository(private val dao: MedicationDao) {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getActiveCoursesFlow(): Flow<List<MedicationCourse>> = dao.getActiveCoursesFlow()

    suspend fun addCourse(course: MedicationCourse) {
        dao.insertCourse(course)
        initDailyIntakes(course)
    }

    private suspend fun initDailyIntakes(course: MedicationCourse) {
        val start = LocalDate.parse(course.startDate)
        repeat(course.durationDays) { day ->
            val date = start.plusDays(day.toLong()).format(formatter)
            dao.insertDailyIntake(DailyIntake(course.id, date))
        }
        val endDate = start.plusDays(course.durationDays.toLong()).format(formatter)
        dao.updateCourse(course.copy(endDate = endDate))  // Добавь endDate в MedicationCourse
    }

    suspend fun markAsTaken(courseId: Long, date: String) {
        val intake = dao.getDailyIntake(courseId, date)?.copy(
            isTaken = true,
            takenTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        ) ?: return
        dao.insertDailyIntake(intake)
    }

    suspend fun getCourseProgress(courseId: Long): CourseProgress {
        val course = dao.getCourseById(courseId) ?: return CourseProgress(
            MedicationCourse(), 0, 0, 0f, null
        )  // ✅ Fetch course
        val intakes = dao.getProgressForCourse(courseId)
        val completed = intakes.count { it.isTaken }
        val total = course.durationDays.coerceAtLeast(1)   // ✅ Используй из course
        val todayDate = LocalDate.now().format(formatter)
        val todayIntake = intakes.find { it.date == todayDate }
        return CourseProgress(
            course, completed, total,
            if (total > 0) (completed / total.toFloat()).coerceIn(0f, 1f) else 0f,
            todayIntake
        )
    }
    // ✅ Новый метод: Flow прогрессов (для избежания suspend в ViewModel)
    fun getActiveCoursesProgressFlow(): Flow<List<CourseProgress>> = getActiveCoursesFlow()
        .flatMapLatest { courses ->
            combine(
                courses.map { course ->
                    flow {
                        emit(getCourseProgress(course.id))
                    }
                }
            ) { progresses -> progresses.toList() }
        }

}