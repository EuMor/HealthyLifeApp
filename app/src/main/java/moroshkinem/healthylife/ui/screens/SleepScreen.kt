package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moroshkinem.healthylife.ui.components.ProgressItem
import moroshkinem.healthylife.ui.viewmodels.SleepViewModel

@Composable
fun SleepScreen(viewModel: SleepViewModel) {
    val sleep by viewModel.todaySleep.collectAsState()

    Column {
        ProgressItem("Сон", Icons.Default.Hotel, sleep / 8f, "$sleep ч", "8 ч", color = MaterialTheme.colorScheme.primary)
        Button(onClick = { viewModel.syncAuto() }) { Text("Синхронизировать") }
        Button(onClick = { viewModel.bedtimeReminder() }) { Text("Напоминание") }
        // Bedtime buttons: "Ложусь" / "Проснулся"
    }
}