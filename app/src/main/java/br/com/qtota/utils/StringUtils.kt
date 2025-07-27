package br.com.qtota.utils

import android.content.Context
import br.com.qtota.R
import br.com.qtota.data.remote.product.MeasureType
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

object StringUtils {

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
        val measure = this
        val (thousandUnit, unit) = when(measureType) {
            MeasureType.WEIGHT -> Pair("kg", "g")
            MeasureType.VOLUME -> Pair("L", "mL")
            MeasureType.LENGTH -> Pair("m", "cm")
        }

        return if(measure >= 1000) {
            "${measure.toDouble()/1000} $thousandUnit"
        } else {
            "$measure $unit"
        }
    }

    fun LocalDate.stringDaysAfterNow(context: Context) : String {
        val difference = ChronoUnit.DAYS.between(LocalDate.now(), this) + 1

        return when {
            difference == 0L -> context.getString(R.string.today)
            difference == 1L -> context.getString(R.string.tomorrow)
            difference > 1L -> context.getString(R.string.diference_days, difference)
            else -> context.getString(R.string.expired)
        }
    }

}