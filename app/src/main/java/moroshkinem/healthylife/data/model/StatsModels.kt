package moroshkinem.healthylife.data.model



// Используется для графика Воды
data class DailyWater(
    val day: String,
    val amount: Int // Вода
)

// Используется для графика Шагов
data class DailySteps(
    val day: String,
    val steps: Int // Шаги
)
data class DailyStats(
        val day: String,      // Например, "Mon"
        val water: Int,       // миллилитров воды
        val steps: Int        // количество шагов
    )