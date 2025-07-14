package br.com.qtota.data.remote.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun write(out: JsonWriter?, value: LocalDate?) {
        out?.value(value?.format(formatter))
    }

    override fun read(`in`: JsonReader?): LocalDate? {
        return LocalDate.parse(`in`?.nextString(), formatter)
    }
}