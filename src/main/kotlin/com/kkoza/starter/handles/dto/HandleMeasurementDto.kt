package com.kkoza.starter.handles.dto

data class HandleMeasurementDto(
        val deviceId: String,
        val handlePosition: Int,
        val temperature: Double,
        val fire: Boolean,
        val burglary: Boolean,
        val frost: Boolean,
        val soundLevel: Double,
        val handleTime: Int
)
