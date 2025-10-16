package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moroshkinem.healthylife.data.model.DailySteps
import moroshkinem.healthylife.data.model.DailyWater

import moroshkinem.healthylife.ui.viewmodels.StatsViewModel
import moroshkinem.healthylife.ui.components.GenericBarChart // ✅ Импорт
import moroshkinem.healthylife.data.model.DailyStats // ✅ Импорт

@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState() // ✅ Создаем состояние прокрутки

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Статистика за неделю", style = MaterialTheme.typography.headlineMedium)

        // 🔹 Список данных по дням
        state.weeklyData.forEach {
            Text("${it.day}: ${it.water} мл / ${it.steps} шагов")
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 График воды
        Text("Потребление воды", style = MaterialTheme.typography.titleMedium)
        GenericBarChart(
            data = state.weeklyData,
            valueSelector = { it.water.toFloat() }, // ✅ Селектор для воды
            labelSelector = { it.day },
            label = "Вода",
            barColor = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Text("График шагов")
        GenericBarChart(
            data = state.weeklyData,
            valueSelector = { it.steps.toFloat() }, // ✅ Селектор для шагов
            labelSelector = { it.day },
            label = "Шаги",
            barColor = MaterialTheme.colorScheme.tertiary
        )

        // 🔹 Карточка общей статистики
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Всего воды: ${state.totalWater} мл")
                Text("Всего шагов: ${state.totalSteps}")
                Spacer(Modifier.height(8.dp))
                Text("Среднее воды: ${state.averageWater} мл/день")
                Text("Среднее шагов: ${state.averageSteps}/день")
            }
        }
    }
}
