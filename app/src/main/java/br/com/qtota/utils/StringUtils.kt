package br.com.qtota.utils

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
        val formato = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formato.format(this)
    }

    fun Int.toDistanceString(): String {
        val distance = this
        return if(distance >= 1000) {
            "${distance.toDouble()/1000} km"
        } else {
            "${distance} m"
        }
    }

    fun Int.toWeightString() : String {
        val weight = this
        return if(weight >= 1000) {
            "${weight.toDouble()/1000} kg"
        } else {
            "${weight} g"
        }
    }

    fun LocalDate.stringDaysAfterNow() : String {
        val difference = ChronoUnit.DAYS.between(this, LocalDate.now())
        return if(difference == 0L) "Hoje" else "$difference dias"
    }

}