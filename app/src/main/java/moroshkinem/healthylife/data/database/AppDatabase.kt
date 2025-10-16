package moroshkinem.healthylife.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import moroshkinem.healthylife.data.local.MealDao
import moroshkinem.healthylife.data.local.MedicationDao
import moroshkinem.healthylife.data.local.SleepDao
import moroshkinem.healthylife.data.local.StepsDao
import moroshkinem.healthylife.data.local.WaterDao
import moroshkinem.healthylife.data.model.DailyIntake
import moroshkinem.healthylife.data.model.Meal
import moroshkinem.healthylife.data.model.Sleep
import moroshkinem.healthylife.data.model.Steps
import moroshkinem.healthylife.data.model.Water
import moroshkinem.healthylife.data.model.MedicationCourse
import moroshkinem.healthylife.data.model.SleepSession

@Database(entities = [Water::class, Steps::class, Meal::class,
    Sleep::class, MedicationCourse::class, SleepSession::class,
    DailyIntake::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun stepsDao(): StepsDao
    abstract fun mealDao(): MealDao
    abstract fun  sleepDao(): SleepDao

    abstract fun medicalDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthy_life_db"
                )
                    .fallbackToDestructiveMigration()  // ✅ Dev: Удалит старые data
                    .addMigrations(MIGRATION_4_5)  // ✅ Production migration
                    .build().also { INSTANCE = it }
                INSTANCE = instance
                instance
            }
        }
    }
}
// ✅ Migration
private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // ✅ CREATE SleepSession
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sleep_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                durationHours REAL NOT NULL,
                qualityScore INTEGER NOT NULL DEFAULT 0,
                stages TEXT
            )
        """)
        // ✅ CREATE Medication
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS medication_courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                startDate TEXT NOT NULL,
                durationDays INTEGER NOT NULL,
                dailyTime TEXT NOT NULL,
                isActive INTEGER NOT NULL DEFAULT 1,
                endDate TEXT
            )
        """)
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS daily_intakes (
                courseId INTEGER NOT NULL,
                date TEXT NOT NULL,
                isTaken INTEGER NOT NULL DEFAULT 0,
                takenTime TEXT,
                PRIMARY KEY(courseId, date),
                FOREIGN KEY(courseId) REFERENCES medication_courses(id) ON DELETE CASCADE
            )
        """)
    }
}