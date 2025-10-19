package moroshkinem.healthylife.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moroshkinem.healthylife.data.model.DailyIntake
import moroshkinem.healthylife.data.model.MedicationCourse
import moroshkinem.healthylife.data.repos.MedicationRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicationViewModel(
    private val repo: MedicationRepository
) : ViewModel() {
    val activeCourses: StateFlow<List<MedicationCourse>> = repo.getActiveCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCourse(course: MedicationCourse) {
        viewModelScope.launch { repo.addCourse(course) }
    }

    fun markToday(courseId: Long) {
        viewModelScope.launch {
            repo.markAsTaken(courseId, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }
    fun getIntakeList(courseId: Long): StateFlow<List<DailyIntake>> {
        return repo.getIntakesForCourse(courseId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
