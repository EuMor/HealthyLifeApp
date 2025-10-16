package moroshkinem.healthylife.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

@Database(entities = [Water::class, Steps::class, Meal::class,
    Sleep::class, MedicationCourse::class,
    DailyIntake::class], version = 5, exportSchema = false)
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
                    .fallbackToDestructiveMigration() // ✅ УБЕДИТЬСЯ, ЧТО ЭТО ЗДЕСЬ!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}