package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.model.Gender
import moroshkinem.healthylife.data.model.UserProfile

class ProfileViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    init {
        loadProfile()
    }

    private fun loadProfile() {
        dataStoreManager.profileFlow
            .onEach { _profile.value = it }
            .launchIn(viewModelScope)
    }

    fun updateWeight(weight: Float) {
        val newProfile = _profile.value.copy(weight = weight)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }

    fun updateHeight(height: Int) {
        val newProfile = _profile.value.copy(height = height)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }

    fun updateAge(age: Int) {
        val newProfile = _profile.value.copy(age = age)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }

    fun updateGender(gender: Gender) {
        val newGender = if (gender.name == "Мужской") {
            moroshkinem.healthylife.data.model.Gender.MALE
        } else {
            moroshkinem.healthylife.data.model.Gender.FEMALE
        }
        val newProfile = _profile.value.copy(gender = newGender)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }

    fun updateStepGoal(steps: Int) {
        val newProfile = _profile.value.copy(dailyStepGoal = steps)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }

    fun updateCalorieGoal(calories: Int) {
        val newProfile = _profile.value.copy(dailyCalorieGoal = calories)
        viewModelScope.launch {
            dataStoreManager.saveProfile(newProfile)
        }
    }
}