package br.com.qtota.utils

import br.com.qtota.data.remote.product.MeasureType
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object StringUtils {

    fun LocalDate.toDDMM() : String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        return format(formatter)
    }

    fun Double.toMonetaryString(): String {
        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
        return formato.format(this)
    }

    fun Int.toDistanceString(): String {
        val distance = this
        return if(distance >= 1000) {
            val distanceKm = String.format(Locale.getDefault(), "%.1f", (distance.toDouble() / 1000))
            "$distanceKm km"
        } else {
            "$distance m"
        }
    }

    fun Int.toMeasureString(measureType: MeasureType) : String {

        val unit = when(measureType) {
            MeasureType.WEIGHT -> Pair("kg", "g")
            MeasureType.VOLUME -> Pair("L", "mL")
            MeasureType.LENGTH -> Pair("m", "cm")
        }
        val measure = this
        return if(measure >= 1000) {
            "${measure.toDouble()/1000} ${unit.first}"
        } else {
            "$measure ${unit.second}"
        }
    }

    fun LocalDate.stringDaysAfterNow() : String {
        val difference = ChronoUnit.DAYS.between(LocalDate.now(), this) + 1

        return if(difference == 0L) "Hoje"
            else if(difference == 1L) "Amanhã"
            else if(difference > 1L) "$difference dias"
            else "Expirado"
    }

}