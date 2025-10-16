package moroshkinem.healthylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep")
data class Sleep(
    @PrimaryKey val date: String,
    val durationHours: Float // Продолжительность
)