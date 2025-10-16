package moroshkinem.healthylife.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moroshkinem.healthylife.ui.components.CircularProgressIndicatorWithLabel
import moroshkinem.healthylife.ui.components.ProgressItem
import moroshkinem.healthylife.ui.components.ProgressRings
import moroshkinem.healthylife.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToWater: () -> Unit,
    navigateToStats: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToMedical:() -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val calories by viewModel.caloriesConsumed.collectAsState()

    val profile by viewModel.profile.collectAsState()

    //val profile by dataStoreManager.profileFlow.collectAsState()
    val caloriesBurned by viewModel.caloriesBurned.collectAsState() // Сожжённые отдельно

    // ✅ Собрать medicationProgresses
    val medicationProgresses by viewModel.medicationProgresses.collectAsState()

    val scrollState = rememberScrollState() // ✅ Создаем состояние прокрутки
    val scrollStateMetrics = rememberScrollState() // ✅ Создаем состояние прокрутки

    val stepsProgress = (steps / profile.dailyStepGoal.toFloat()).coerceIn(0f, 1f)
    val caloriesProgress = (calories / profile.dailyCalorieGoal.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = navigateToProfile,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Профиль"
                        )
                    },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Твой прогресс", style = MaterialTheme.typography.headlineMedium)

//            CircularProgressIndicatorWithLabel(
//                progress = state.progress,
//                waterAmount = state.amount,
//                goal = state.goal
//            )
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                StatsItem(label = "Шаги", value = "$steps")
//                StatsItem(label = "Калории", value = "$calories")
//            }
            // 👉 Горизонтальный список колец с подписями
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProgressItem(
                    label = "Вода",
                    icon = Icons.Default.LocalDrink,
                    progress = state.waterProgress,
                    valueText = "${state.waterAmount} мл",
                    goalText = "${state.waterGoal} мл",
                    color = MaterialTheme.colorScheme.primary
                )
                ProgressItem(
                    label = "Шаги",
                    icon = Icons.Default.DirectionsWalk,
                    progress = stepsProgress,
                    valueText = steps.toString(),
                    goalText = profile.dailyStepGoal.toString(),
                    color = MaterialTheme.colorScheme.tertiary
                )
                ProgressItem(
                    label = "Калории",
                    icon = Icons.Default.LocalFireDepartment,
                    progress = caloriesProgress,
                    valueText = state.caloriesConsumed.toString(),
                    goalText = state.calorieGoal.toString(),
                    color = MaterialTheme.colorScheme.secondary
                )
                ProgressItem(
                    label = "Сон",
                    icon = Icons.Default.Hotel,
                    progress = state.sleepProgress,
                    valueText = String.format("%.1f ч", state.sleepHours),
                    goalText = String.format("%.1f ч", state.sleepGoal),
                    color = Color(0xFF8B00FF)
                )
            }
            Spacer(Modifier.height(16.dp))

            // В Row с прогрессами
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(medicationProgresses) { progress ->  // ✅ items emit'ит composables
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ProgressItem(
                            label = progress.course.name,
                            icon = Icons.Default.LocalPharmacy,
                            progress = progress.progress,
                            valueText = "${progress.completedDays}",
                            goalText = "${progress.totalDays}",
                            color = if (progress.todayIntake?.isTaken == true) Color.Green else MaterialTheme.colorScheme.error
                        )
                        if (progress.todayIntake?.isTaken == false) {
                            Button(
                                onClick = { viewModel.markMedicationToday(progress.course.id) }
                            ) {
                                Text("Принял")
                            }
                        }
                    }
                }
            }

            ProgressRings(
                waterProgress = state.waterProgress,
                stepsProgress = stepsProgress,
                caloriesProgress = caloriesProgress,
                sleepProgress = state.sleepProgress      // ✅ Новая метрика
            )

            Spacer(Modifier.height(24.dp))

            // Метрики
            Row(Modifier.fillMaxWidth().horizontalScroll(scrollStateMetrics), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Вода
                StatsItem("Вода", "${state.waterAmount}/${state.waterGoal} мл")
                Spacer(Modifier.width(24.dp))
                StatsItem("Калории", "${state.caloriesConsumed - caloriesBurned}/${state.calorieGoal}")
                Spacer(Modifier.width(24.dp))
                // Шаги
                StatsItem("Шаги", "$steps/${profile.dailyStepGoal}")
                Spacer(Modifier.width(24.dp))
                // Калории
                StatsItem("Еда/Сож.", "${state.caloriesConsumed}/${state.calorieGoal} кал")
                Spacer(Modifier.width(24.dp))
                // Сон
                StatsItem("Сон", String.format("%.1f / %.1f ч", state.sleepHours, state.sleepGoal))
            }
            Button(onClick = navigateToWater) {
                Text("Добавить воду")
            }

            OutlinedButton(onClick = navigateToStats) {
                Text("Посмотреть статистику")
            }

            Button(
                onClick = viewModel::resetTodayStats,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("СБРОС СТАТИСТИКИ (DEBUG)")
            }
            Button(onClick = navigateToMedical) {
                Text("Добавить курс таблеток")
            }
        }
    }
}

@Composable
private fun StatsItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}