package com.kkoza.starter.nodes.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.kkoza.starter.config.CustomJodaDateTimeDeserializer
import com.kkoza.starter.config.CustomJodaDateTimeSerializer
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.dto.Temperature
import org.joda.time.DateTime

data class NodeMeasurementList(
        val count: Long,
        val limit: Int?,
        val offset: Int?,
        val measurements: List<NodeMeasurement>
)

data class NodeMeasurement(
        val id: String? = null,

        @JsonSerialize(using = CustomJodaDateTimeSerializer::class)
        @JsonDeserialize(using = CustomJodaDateTimeDeserializer::class)
        val date: DateTime,

        val nodeName: String,
        val deviceId: String,
        val temperature: Temperature,
        val lightIntensity: LightIntensity,
        val humidity: Humidity,
        val motion: Boolean,
        val carbonDioxide: CarbonDioxide
)

data class Humidity(
        val value: Double,
        val unit: String = "jednostka?"
)

data class LightIntensity(
        val value: Double,
        val unit: String = "Lumeny?"
)

data class CarbonDioxide(
        val value: Double,
        val unit: String = "CO2"
)