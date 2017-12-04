package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementList

class MeasurementFacade(
        private val measurementOperation: MeasurementOperation
) {

    fun add(measurementDocument: MeasurementDocument): String {
        return measurementOperation.add(measurementDocument)
    }

    fun get(userId: String, sort: String?, offset: Int?, limit: Int?): MeasurementList {
        return measurementOperation.get(userId, sort, offset, limit)
    }

    fun deleteById(id: String) {
        measurementOperation.deleteById(id)
    }
}