package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.repos.SleepRepository

class SleepViewModel(
    private val sleepRepository: SleepRepository
) : ViewModel() {

    val todaySleep: StateFlow<Float> = sleepRepository.getTodaySleepFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

    fun saveSleep(hours: Float) {
        viewModelScope.launch {
            sleepRepository.saveSleep(hours)
        }
    }
}