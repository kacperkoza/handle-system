package com.kkoza.starter.nodes.dto

import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.dto.Temperature
import org.joda.time.DateTime

data class NodeMeasurementList(
        val count: Int,
        val limit: Int?,
        val offset: Int?,
        val nodeMeasurements: List<NodeMeasurement>,
        val handles: List<DeviceDto>)

data class NodeMeasurement(
        val id: String? = null,
        val date: DateTime,
        val nodeName: String,
        val nodeId: String,
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