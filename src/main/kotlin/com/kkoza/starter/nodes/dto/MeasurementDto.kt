package com.kkoza.starter.nodes.dto

data class NodeMeasurementDto(
        val deviceId: String,
        val temperature: Double,
        val lightIntensity: Double,
        val humidity: Double,
        val motion: Boolean,
        val carbonDioxide: Double
)