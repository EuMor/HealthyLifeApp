package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import moroshkinem.healthylife.data.repos.WaterRepository
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.repos.MealRepository
import moroshkinem.healthylife.data.repos.MedicationRepository
import moroshkinem.healthylife.data.repos.SleepRepository
import moroshkinem.healthylife.data.repos.StepsRepository
import moroshkinem.healthylife.data.sensors.StepCounterManager

class HomeViewModelFactory(
    private val waterRepository: WaterRepository,
    private val dataStoreManager: DataStoreManager,
    private val stepCounterManager: StepCounterManager,
    private val stepsRepository: StepsRepository,
    private val mealRepository: MealRepository,   // ✅ ДОБАВЛЕНО
    private val sleepRepository: SleepRepository,  // ✅ ДОБАВЛЕНО
    private val medicationRepository: MedicationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                waterRepository,
                dataStoreManager,
                stepCounterManager,
                stepsRepository, // ✅ Передаем
                mealRepository,  // ✅ Передаем
                sleepRepository,  // ✅ Передаем
                medicationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}