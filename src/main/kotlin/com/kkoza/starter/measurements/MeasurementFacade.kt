package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementList

class MeasurementFacade(
        private val measurementOperation: MeasurementOperation
) {

    fun add(measurement: Measurement) {
        measurementOperation.add(measurement)
    }

    fun get(sort: String?, offset: Int?, limit: Int?): MeasurementList {
        return measurementOperation.get(sort, offset, limit)
    }
}