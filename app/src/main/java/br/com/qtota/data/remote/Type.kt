package br.com.qtota.data.remote

import com.google.gson.TypeAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun write(out: com.google.gson.stream.JsonWriter?, value: LocalDate?) {
        out?.value(value?.format(formatter))
    }

    override fun read(`in`: com.google.gson.stream.JsonReader?): LocalDate? {
        return LocalDate.parse(`in`?.nextString(), formatter)
    }
}