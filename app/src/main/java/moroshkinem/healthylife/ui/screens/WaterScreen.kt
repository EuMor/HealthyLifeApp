package moroshkinem.healthylife.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import moroshkinem.healthylife.ui.viewmodels.WaterViewModel


@Composable
fun WaterScreen(viewModel: WaterViewModel) {
    val amount by viewModel.amount.collectAsState()
    val goal by viewModel.goal.collectAsState()
    val progress by viewModel.progress.collectAsState()

    var inputAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Цель: $goal мл", style = MaterialTheme.typography.titleLarge)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(20.dp)
        )
        Text("Сегодня выпито: $amount мл", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(32.dp))

        TextField(
            value = inputAmount,
            onValueChange = { inputAmount = it },
            label = { Text("Мл") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = {
                val ml = inputAmount.toIntOrNull() ?: 0
                if (ml > 0) {
                    viewModel.addWater(ml)
                    inputAmount = ""
                }
            }) {
                Text("Добавить")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = {
                // Можно открыть диалог
                viewModel.setWaterGoal(2500)
            }) {
                Text("Изменить цель")
            }
        }
    }
}