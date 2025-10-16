package moroshkinem.healthylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "steps")
data class Steps(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val count: Int,
    val baselineCount: Int = 0 // ✅ Базовое значение на начало дня
)