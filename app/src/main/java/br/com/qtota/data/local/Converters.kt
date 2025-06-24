package br.com.qtota.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {

    @TypeConverter
    fun fromLocalDateToEpoch(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromEpochToLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }
}
