package moroshkinem.healthylife.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.repos.WaterRepository


class WaterViewModel(
    private val repository: WaterRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _amount = MutableStateFlow(0)
    val amount: StateFlow<Int> = _amount

    private val _goal = MutableStateFlow(2000)
    val goal: StateFlow<Int> = _goal

    // ✅ Правильно: combine + stateIn
    val progress: StateFlow<Float> = _amount
        .combine(_goal) { amount, goal ->
            (amount / goal.toFloat()).coerceIn(0f, 1f)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

    init {
        loadTodayWater()
        observeGoal()
    }

    private fun loadTodayWater() {
        viewModelScope.launch {
            val water = repository.getTodayWater()
            _amount.value = water?.amountInMl ?: 0
        }
    }

    private fun observeGoal() {
        dataStoreManager.waterGoalFlow
            .onEach { goal ->
                _goal.value = goal
            }
            .launchIn(viewModelScope)
    }

    fun addWater(amountInMl: Int) {
        viewModelScope.launch {
            repository.addWater(amountInMl)
            loadTodayWater()
        }
    }

    fun setWaterGoal(goal: Int) {
        viewModelScope.launch {
            dataStoreManager.setWaterGoal(goal)
        }
    }
}