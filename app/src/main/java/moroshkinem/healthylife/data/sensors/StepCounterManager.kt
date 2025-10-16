package moroshkinem.healthylife.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.dataStore
import moroshkinem.healthylife.data.repos.StepsRepository
import java.time.LocalDate

class StepCounterManager(
    private val context: Context,
    private val dataStoreManager: DataStoreManager,
    private val stepsRepository: StepsRepository
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private var lastBootSteps = 0f
    private var today = LocalDate.now().toString()

    // Ключ для хранения начального значения шагов
    companion object {
        private val STEPS_START_KEY = floatPreferencesKey("steps_start_value")
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val totalSteps = event.values[0]

            val currentDay = LocalDate.now().toString()
            if (currentDay != today) {
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[STEPS_START_KEY] = totalSteps
                    }
                }
                lastBootSteps = totalSteps
                today = currentDay
                _todaySteps.value = 0
                return
            }

            val stepsToday = (totalSteps - lastBootSteps).toInt()
            val clampedSteps = stepsToday.coerceAtLeast(0)
            _todaySteps.value = clampedSteps

            // ✅ Сохраняем в БД
            scope.launch {
                stepsRepository.saveTodaySteps(clampedSteps)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    fun start() {
        // Загружаем начальное значение из DataStore
        scope.launch {
            val startValue = context.dataStore.data
                .map { it[STEPS_START_KEY] ?: 0f }
                .first() // suspend-функция, ждёт первое значение
//                .also { startValue ->
//                    lastBootSteps = startValue
//                }
            lastBootSteps = startValue
            today = LocalDate.now().toString()
        }

        stepCounterSensor?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun resetStepsStartValue() {
        scope.launch {
            // Мы не знаем текущее значение сенсора, но сбросим DataStore,
            // чтобы при следующем onSensorChanged оно обновилось
            context.dataStore.edit { settings ->
                settings[STEPS_START_KEY] = 0f // Или лучше: текущее значение totalSteps
            }
        }
        // Чтобы мгновенно обновить UI
        _todaySteps.value = 0
    }

    fun stop() {
        sensorManager.unregisterListener(sensorListener)
    }
}