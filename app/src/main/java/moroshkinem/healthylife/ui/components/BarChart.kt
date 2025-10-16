package moroshkinem.healthylife.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

import moroshkinem.healthylife.data.model.DailyWater // ✅ Новый импорт
@Composable
fun WaterBarChart(
    data: List<DailyWater>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                description.text = "Потребление воды за неделю"
                legend.isEnabled = false
                setDrawGridBackground(false)
                setTouchEnabled(true)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in data.indices) data[index].day else ""
                    }
                }

                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false

                animateY(1000, Easing.EaseInOutQuad)
            }
        },
        update = { chart ->
            // ✅ Создаём новые Entry
            val entries = data.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.amount.toFloat())
            }

            // ✅ ВСЕГДА создаём новый DataSet (нельзя мутировать старый)
            val dataSet = BarDataSet(entries, "мл").apply {
                color = android.graphics.Color.BLUE
                valueTextSize = 12f
            }

            // ✅ Создаём новые BarData
            val barData = BarData(dataSet).apply {
                setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${value.toInt()}"
                })
            }

            // ✅ Присваиваем данные
            chart.data = barData

            // ✅ Обязательно: уведомляем об изменении
            chart.notifyDataSetChanged()

            // ✅ Перерисовываем
            chart.invalidate()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}