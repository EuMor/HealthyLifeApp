package moroshkinem.healthylife.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressRings(
    waterProgress: Float,
    stepsProgress: Float,
    caloriesProgress: Float,
    sleepProgress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(240.dp)
    ) {
        // Фоновые круги (20dp)
        CircularProgressIndicator(
            progress = 1f,
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 20.dp,
            modifier = Modifier.size(240.dp)
        )

        // Вода (Primary, 20dp)
        CircularProgressIndicator(
            progress = waterProgress,
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 20.dp,
            modifier = Modifier.size(240.dp)
        )

        // Шаги (Tertiary, 20dp, немного меньше)
        CircularProgressIndicator(
            progress = stepsProgress,
            color = MaterialTheme.colorScheme.tertiary,
            strokeWidth = 20.dp,
            modifier = Modifier.size(200.dp)
        )

        // Калории (Secondary, 20dp, еще меньше)
        CircularProgressIndicator(
            progress = caloriesProgress,
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 20.dp,
            modifier = Modifier.size(160.dp)
        )

        // Сон (Новый цвет, 20dp, самый маленький)
        CircularProgressIndicator(
            progress = sleepProgress,
            color = Color(0xFF8B00FF), // Фиолетовый
            strokeWidth = 20.dp,
            modifier = Modifier.size(120.dp)
        )

        // Центральный текст
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Прогресс", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("за день", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}