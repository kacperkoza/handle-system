package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.*
import org.joda.time.DateTime

class MeasurementFacade(
        private val measurementOperation: MeasurementOperation,
        private val graphDataProvider: GraphDataProvider
) {

    fun add(handleMeasurementDocument: HandleMeasurementDocument): String {
        return measurementOperation.add(handleMeasurementDocument)
    }

    fun get(userId: String, sort: MeasurementSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?): MeasurementList {
        return measurementOperation.get(userId, sort, offset, limit, alarms, handles)
    }

    fun deleteById(id: String) {
        measurementOperation.deleteById(id)
    }

    fun getGraphDataList(startDate: DateTime?, endDate: DateTime?, fieldName: FieldFilter, handleId: String): ItemsDto {
        return graphDataProvider.getGraphDataList(startDate, endDate, fieldName, handleId)
    }

    fun findOneFromAllHandles(userId: String): List<HandleMeasurement> {
        return measurementOperation.findOneFromEveryHandle(userId)
    }

    //todo: start here with nodes

}