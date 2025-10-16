package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import moroshkinem.healthylife.ui.viewmodels.MealViewModel

@Composable
fun MealsScreen(viewModel: MealViewModel) {
    val todayCalories by viewModel.todayCalories.collectAsState()
    var caloriesInput by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Сегодня съедено: $todayCalories ккал", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = caloriesInput,
            onValueChange = { caloriesInput = it },
            label = { Text("Калории") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Описание (опционально)") }
        )

        Button(onClick = {
            val cal = caloriesInput.toIntOrNull() ?: 0
            if (cal > 0) {
                viewModel.addMeal(cal, if (desc.isNotBlank()) desc else null)
                caloriesInput = ""; desc = ""
            }
        }) {
            Text("Добавить приём пищи")
        }
    }
}