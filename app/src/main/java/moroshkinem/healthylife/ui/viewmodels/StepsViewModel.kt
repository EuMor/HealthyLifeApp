package moroshkinem.healthylife.ui.viewmodels

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.repos.StepsRepository
import java.time.LocalDate

class StepsViewModel(
    application: Application,
    private val stepsRepository: StepsRepository
) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private var lastSavedDate = LocalDate.now()
    private var totalStepsSinceBoot = 0

    init {
        // Загружаем шаги за сегодня
        viewModelScope.launch {
            _steps.value = stepsRepository.getTodaySteps()
        }

        // Запускаем слушатель сенсора
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                totalStepsSinceBoot = it.values[0].toInt()

                // Проверяем, не наступил ли новый день
                val currentDate = LocalDate.now()
                if (currentDate != lastSavedDate) {
                    // Новый день - сбрасываем baseline
                    viewModelScope.launch {
                        stepsRepository.resetBaselineForNewDay(totalStepsSinceBoot)
                        lastSavedDate = currentDate
                        _steps.value = 0
                    }
                } else {
                    // Обновляем шаги
                    viewModelScope.launch {
                        stepsRepository.saveTodaySteps(totalStepsSinceBoot)
                        _steps.value = stepsRepository.getTodaySteps()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не требуется
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }

    fun resetSteps() {
        viewModelScope.launch {
            stepsRepository.clearTodaySteps()
            _steps.value = 0
        }
    }
}