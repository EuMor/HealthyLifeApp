package moroshkinem.healthylife.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun <T> GenericBarChart( // ✅ Принимает универсальный тип T
    data: List<T>,
    valueSelector: (T) -> Float, // ✅ Функция для извлечения значения
    labelSelector: (T) -> String, // ✅ Функция для извлечения подписи
    label: String, // Заголовок
    barColor: ComposeColor,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp
) {
    val context = LocalContext.current
    val barColorInt = barColor.toArgb()
    val onSurfaceColorInt = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        factory = {
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setScaleEnabled(false)
                setTouchEnabled(false)
                setDrawGridBackground(false)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = Color.DKGRAY
                xAxis.granularity = 1f
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return data.getOrNull(index)?.let(labelSelector) ?: "" // ✅ Используем селектор
                    }
                }
                axisLeft.setDrawGridLines(false)
                axisLeft.textColor = Color.DKGRAY
                axisLeft.axisMinimum = 0f

                axisRight.isEnabled = false
                animateY(1000, Easing.EaseInOutExpo)
            }
        },
        update = { chart ->
            // ✅ Используем valueSelector для создания BarEntry
            val entries = data.mapIndexed { index, item ->
                BarEntry(index.toFloat(), valueSelector(item))
            }

            val dataSet = BarDataSet(entries, label).apply {
                color = barColorInt
                valueTextColor = onSurfaceColorInt
                valueTextSize = 12f
            }
            // ... (остальной код update)
            chart.data = BarData(dataSet).apply { barWidth = 0.5f }
            chart.invalidate()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}