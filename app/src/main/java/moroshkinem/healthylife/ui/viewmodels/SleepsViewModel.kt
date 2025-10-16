package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.repos.SleepRepository

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
) : ViewModel() {

    val todaySleep: StateFlow<Float> = sleepRepository.getTodaySleepFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )
    fun manualInput(hours: Float) {
        viewModelScope.launch { sleepRepository.saveSleep(hours) }  // ✅ launch
    }

    fun syncAuto() {
        viewModelScope.launch { sleepRepository.syncFromHealthConnect() }  // ✅ launch
    }

    fun bedtimeReminder() {
        viewModelScope.launch { sleepRepository.sendNotification("Время спать!") }  // ✅ Add method
    }
}