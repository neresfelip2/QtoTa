package br.com.qtota.data.remote.adapters

import br.com.qtota.data.remote.product.MeasureType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class MeasureTypeAdapter : TypeAdapter<MeasureType>() {

    override fun write(out: JsonWriter, value: MeasureType) {
        out.value(value.name)
    }

    override fun read(`in`: JsonReader): MeasureType? {
        return MeasureType.valueOf(`in`.nextString())
    }
}