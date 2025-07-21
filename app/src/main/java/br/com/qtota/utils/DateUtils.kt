package br.com.qtota.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {

    private val ddMMFormat = DateTimeFormatter.ofPattern("dd/MM")

    fun LocalDate.toDDMM() : String {
        return this.format(ddMMFormat)
    }

}