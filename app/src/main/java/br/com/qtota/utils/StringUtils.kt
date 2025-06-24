package br.com.qtota.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StringUtils {

    fun LocalDate.toDDMM() : String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        return format(formatter)
    }

}