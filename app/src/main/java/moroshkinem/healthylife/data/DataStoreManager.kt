package moroshkinem.healthylife.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import moroshkinem.healthylife.data.model.Gender
import moroshkinem.healthylife.data.model.UserProfile

// Расширение для получения DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    // Все ключи в одном месте
    private object Keys {
        val WEIGHT = floatPreferencesKey("weight")
        val HEIGHT = intPreferencesKey("height")
        val AGE = intPreferencesKey("age")
        val GENDER = stringPreferencesKey("gender")
        val DAILY_STEP_GOAL = intPreferencesKey("daily_step_goal")
        val DAILY_CALORIE_GOAL = intPreferencesKey("daily_calorie_goal")
        val WATER_GOAL_ML = intPreferencesKey("water_goal_ml")

        // Ключ для хранения "начального значения" шагов при старте дня
        val STEPS_START_KEY = floatPreferencesKey("steps_start_value") // ✅ Добавлено!

        val DAILY_SLEEP_GOAL = floatPreferencesKey("daily_sleep_goal") // 8.0f часов

        val STEP_BASE_KEY = intPreferencesKey("step_base_count")

        val STEP_BASE_DATE_KEY = stringPreferencesKey("step_base_date")
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { settings ->
            settings[Keys.WEIGHT] = profile.weight
            settings[Keys.HEIGHT] = profile.height
            settings[Keys.AGE] = profile.age
            settings[Keys.GENDER] = profile.gender.name
            settings[Keys.DAILY_STEP_GOAL] = profile.dailyStepGoal
            settings[Keys.DAILY_CALORIE_GOAL] = profile.dailyCalorieGoal
        }
    }

    val profileFlow = context.dataStore.data.map { settings ->
        UserProfile(
            weight = settings[Keys.WEIGHT] ?: 70f,
            height = settings[Keys.HEIGHT] ?: 175,
            age = settings[Keys.AGE] ?: 25,
            gender = Gender.valueOf(settings[Keys.GENDER] ?: Gender.MALE.name),
            dailyStepGoal = settings[Keys.DAILY_STEP_GOAL] ?: 10000,
            dailyCalorieGoal = settings[Keys.DAILY_CALORIE_GOAL] ?: 2000
        )
    }

    suspend fun setWaterGoal(amount: Int) {
        context.dataStore.edit { settings ->
            settings[Keys.WATER_GOAL_ML] = amount
        }
    }

    val waterGoalFlow = context.dataStore.data.map { settings ->
        settings[Keys.WATER_GOAL_ML] ?: 2000
    }
}