package moroshkinem.healthylife.data.database

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromString(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun toString(date: LocalDate?): String? = date?.toString()
}