package com.kkoza.starter.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class CustomJodaDateTimeSerializer : StdSerializer<DateTime>(DateTime::class.java) {

    private val datePattern = "dd-MM-yyyy HH:mm"

    override fun serialize(value: DateTime, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.toString(datePattern))
    }

}

class CustomJodaDateTimeDeserializer : JsonDeserializer<DateTime>() {

    private val dateTimeFormat = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DateTime {
        val str = p.text.trim()
        return dateTimeFormat.parseDateTime(str)
    }

}