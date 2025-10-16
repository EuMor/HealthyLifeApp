package moroshkinem.healthylife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import moroshkinem.healthylife.data.model.DailyIntake
import moroshkinem.healthylife.data.model.MedicationCourse

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: MedicationCourse)

    @Update
    suspend fun updateCourse(course: MedicationCourse)

    @Query("SELECT * FROM medication_courses WHERE isActive = 1")
    fun getActiveCoursesFlow(): Flow<List<MedicationCourse>>

    @Query("SELECT * FROM medication_courses WHERE id = :id")
    suspend fun getCourseById(id: Long): MedicationCourse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyIntake(intake: DailyIntake)

    @Query("SELECT * FROM daily_intakes WHERE courseId = :courseId AND date = :date")
    suspend fun getDailyIntake(courseId: Long, date: String): DailyIntake?

    @Query("SELECT * FROM daily_intakes WHERE courseId = :courseId ORDER BY date")
    suspend fun getIntakesForCourse(courseId: Long): List<DailyIntake>

    @Query("SELECT * FROM daily_intakes WHERE courseId = :courseId AND date = :date")
    fun getDailyIntakeFlow(courseId: Long, date: String): Flow<DailyIntake?>
}