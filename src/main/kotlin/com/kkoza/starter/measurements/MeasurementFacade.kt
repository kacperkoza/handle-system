package com.kkoza.starter.measurements

class MeasurementFacade(
        private val measurementOperation: MeasurementOperation
) {

    fun add(measurement: Measurement) {
        measurementOperation.add(measurement)
    }

    fun findAll(): List<Measurement> {
        return measurementOperation.findAll()
    }
}