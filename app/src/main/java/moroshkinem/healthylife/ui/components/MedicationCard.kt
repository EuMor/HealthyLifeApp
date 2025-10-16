package moroshkinem.healthylife.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moroshkinem.healthylife.data.model.CourseProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationCard(
    progress: CourseProgress,
    onMarkTaken: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (progress.todayIntake?.isTaken == true) Color.Green.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.LocalPharmacy,
                contentDescription = progress.course.name,
                tint = if (progress.todayIntake?.isTaken == true) Color.Green else MaterialTheme.colorScheme.error
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(progress.course.name, style = MaterialTheme.typography.titleMedium)
                Text("День ${progress.completedDays} из ${progress.totalDays}", style = MaterialTheme.typography.bodySmall)
                LinearProgressIndicator(
                    progress = progress.progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (progress.todayIntake?.isTaken == true) Color.Green else MaterialTheme.colorScheme.error
                )
            }
            if (progress.todayIntake?.isTaken != true) {
                Button(onClick = onMarkTaken) {
                    //Log.d("HomeScreen", "Button clicked for ${progress.course.id}")
                    Text("Принял")
                }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = "Принято", tint = Color.Green)
            }
        }
    }
}