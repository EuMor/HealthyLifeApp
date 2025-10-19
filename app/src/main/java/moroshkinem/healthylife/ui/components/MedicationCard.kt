package moroshkinem.healthylife.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moroshkinem.healthylife.data.model.CourseProgress
import moroshkinem.healthylife.data.model.MedicationCourse
import moroshkinem.healthylife.ui.viewmodels.MedicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationCourseCard(
    progress: CourseProgress,
    viewModel: MedicationViewModel,
    onMarkTaken: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val intakes by viewModel.getIntakeList(progress.course.id).collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ▶️ Название и прогресс в 1 строке
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(progress.course.name, style = MaterialTheme.typography.titleMedium)
                Text("${progress.completedDays}/${progress.totalDays}",
                    style = MaterialTheme.typography.labelMedium)
            }

            Spacer(Modifier.height(4.dp))

            // 💊 ПрогрессBar
            LinearProgressIndicator(
                progress = progress.progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (progress.todayIntake?.isTaken == true) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(8.dp))

            // Кнопка "Принял"
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (progress.todayIntake?.isTaken == true) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Принято",
                        tint = Color(0xFF4CAF50)
                    )
                } else {
                    Button(onClick = onMarkTaken) {
                        Text("Принял")
                    }
                }
            }

            // 📅 История
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                intakes.forEach { intake ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(intake.date, style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (intake.isTaken) "✅ ${intake.takenTime ?: ""}" else "❌ Пропуск",
                            color = if (intake.isTaken) Color(0xFF4CAF50) else Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}