package com.kkoza.starter.handles.dto

import com.kkoza.starter.devices.api.DeviceDto

import com.kkoza.starter.handles.Alarm
import com.kkoza.starter.handles.HandlePosition
import org.joda.time.DateTime

data class HandleMeasurement(
        val id: String? = null,
        val date: DateTime,
        val handleName: String,
        val handleId: String,
        val handlePosition: HandlePosition,
        val temperature: Temperature,
        val alarm: Alarm,
        val soundLevel: SoundLevel,
        val handleTime: Int //wtf?
)

data class MeasurementList(
        val count: Int,
        val limit: Int?,
        val offset: Int?,
        val handleMeasurements: List<HandleMeasurement>,
        val handles: List<DeviceDto>)

data class SoundLevel(
        val value: Double,
        val unit: String = "dB"
)

data class Temperature(
        val value: Double,
        val unit: String = "°C"
)