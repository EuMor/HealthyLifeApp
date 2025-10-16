package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import moroshkinem.healthylife.data.model.Gender
import moroshkinem.healthylife.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val profile by viewModel.profile.collectAsState()

    var weightInput by remember { mutableStateOf(profile.weight.toString()) }
    var heightInput by remember { mutableStateOf(profile.height.toString()) }
    var ageInput by remember { mutableStateOf(profile.age.toString()) }
    var stepGoalInput by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var calorieGoalInput by remember { mutableStateOf(profile.dailyCalorieGoal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Профиль", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text("Вес (кг)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = heightInput,
            onValueChange = { heightInput = it },
            label = { Text("Рост (см)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = ageInput,
            onValueChange = { ageInput = it },
            label = { Text("Возраст") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Пол
        Row {
            Text("Пол: ")
            RadioButton(
                selected = profile.gender == Gender.MALE,
                onClick = { viewModel.updateGender(Gender.MALE) }
            )
            Text("Мужской")

            Spacer(Modifier.width(16.dp))

            RadioButton(
                selected = profile.gender == Gender.FEMALE,
                onClick = { viewModel.updateGender(Gender.FEMALE) }
            )
            Text("Женский")
        }

        OutlinedTextField(
            value = stepGoalInput,
            onValueChange = { stepGoalInput = it },
            label = { Text("Цель по шагам") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = calorieGoalInput,
            onValueChange = { calorieGoalInput = it },
            label = { Text("Цель по калориям") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            viewModel.updateWeight(weightInput.toFloatOrNull() ?: profile.weight)
            viewModel.updateHeight(heightInput.toIntOrNull() ?: profile.height)
            viewModel.updateAge(ageInput.toIntOrNull() ?: profile.age)
            viewModel.updateStepGoal(stepGoalInput.toIntOrNull() ?: profile.dailyStepGoal)
            viewModel.updateCalorieGoal(calorieGoalInput.toIntOrNull() ?: profile.dailyCalorieGoal)
        }) {
            Text("Сохранить")
        }
    }
}