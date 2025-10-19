package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.repos.MedicationRepository
import moroshkinem.healthylife.ui.viewmodels.MedicationViewModel
import java.time.LocalDate
import moroshkinem.healthylife.data.database.AppDatabase  // ✅ Импорт DB
import moroshkinem.healthylife.data.model.MedicationCourse  // ✅ Импорт модели
import moroshkinem.healthylife.ui.viewmodels.WaterViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)  // ✅ Для Date/Time Pickers
@Composable
fun AddMedicationScreen(
//    onCourseAdded: () -> Unit,
//    // ✅ Manual factory с DI (Hilt лучше, но для простоты)
//    viewModel: MedicationViewModel = viewModel(
//        factory = MedicationViewModelFactory(
//            MedicationRepository(
//                dao = LocalContext.current.appDatabase.medicationDao(),  // ✅ Inject DB
//                context = LocalContext.current  // ✅ Context
//            )
//        )
//    )
            viewModel: MedicationViewModel
) {
    var name by remember { mutableStateOf("") }
    var durationDays by remember { mutableStateOf("30") }
    var startDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var dailyTime by remember { mutableStateOf("09:00") }

    // ✅ DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now().toEpochDay() * 86400000L
    )

    // ✅ TimePicker
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0
    )

    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Добавить курс лекарств", style = MaterialTheme.typography.headlineMedium)

        // Название
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название (например, Витамин D)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Длительность
        OutlinedTextField(
            value = durationDays,
            onValueChange = { durationDays = it },
            label = { Text("Длительность (дней)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Дата начала
        OutlinedTextField(
            value = startDate,
            onValueChange = { },
            label = { Text("Дата начала") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        TextButton(onClick = { showDateDialog = true }) {
            Text("Выбрать дату")
        }

        // Время напоминания
        OutlinedTextField(
            value = dailyTime,
            onValueChange = { },
            label = { Text("Время напоминания") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        TextButton(onClick = { showTimeDialog = true }) {
            Text("Выбрать время")
        }

        // Кнопка добавления
        Button(
            onClick = {
                val course = MedicationCourse(
                    name = name.trim(),
                    startDate = startDate,
                    durationDays = durationDays.toIntOrNull() ?: 30,
                    dailyTime = dailyTime
                )
                if (course.name.isNotBlank() && course.durationDays > 0) {
                    viewModel.addCourse(course)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && durationDays.toIntOrNull()?.let { it > 0 } == true
        ) {
            Text("Добавить курс")
        }
    }

    // ✅ DatePicker Dialog
    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val epochMillis = datePickerState.selectedDateMillis ?: return@TextButton
                        startDate = DateTimeFormatter.ISO_LOCAL_DATE.format(
                            LocalDate.ofEpochDay(epochMillis / 86400000L)
                        )
                        showDateDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ✅ TimePicker Dialog
    if (showTimeDialog) {
        TimePickerDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Выберите время напоминания") },  // ✅ Title
            confirmButton = {
                TextButton(
                    onClick = {
                        dailyTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimeDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) { Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)  // ✅ Content lambda
        }
    }
}
class MedicationViewModelFactory(
    private val repository: MedicationRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            return MedicationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}