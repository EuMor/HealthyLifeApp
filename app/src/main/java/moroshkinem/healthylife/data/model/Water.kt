package moroshkinem.healthylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "water_intake")
data class Water(
    @PrimaryKey val date: String,
    val amountInMl: Int
)