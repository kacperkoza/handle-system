package com.kkoza.starter.handles.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.kkoza.starter.config.CustomJodaDateTimeDeserializer
import com.kkoza.starter.config.CustomJodaDateTimeSerializer
import com.kkoza.starter.handles.Alarm
import com.kkoza.starter.handles.HandlePosition
import org.joda.time.DateTime

data class HandleMeasurement(
        val id: String? = null,

        @JsonSerialize(using = CustomJodaDateTimeSerializer::class)
        @JsonDeserialize(using = CustomJodaDateTimeDeserializer::class)
        val date: DateTime,

        val handleName: String,
        val deviceId: String,
        val handlePosition: HandlePosition,
        val temperature: Temperature,
        val alarm: Alarm,
        val soundLevel: SoundLevel,
        val handleTime: Int //wtf?
)

data class MeasurementList(
        val count: Long,
        val limit: Int?,
        val offset: Int?,
        val handleMeasurements: List<HandleMeasurement>
)

data class SoundLevel(
        val value: Double,
        val unit: String = "dB"
)

data class Temperature(
        val value: Double,
        val unit: String = "°C"
)