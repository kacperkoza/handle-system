package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementList

class MeasurementFacade(
        private val measurementOperation: MeasurementOperation
) {

    fun add(measurement: Measurement): String {
        return measurementOperation.add(measurement)
    }

    fun get(userId: String, sort: String?, offset: Int?, limit: Int?): MeasurementList {
        return measurementOperation.get(userId, sort, offset, limit)
    }

    fun deleteById(id: String) {
        measurementOperation.deleteById(id)
    }
}