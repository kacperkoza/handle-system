package com.kkoza.starter.handles

import com.kkoza.starter.graphs.HandleFieldFilter
import com.kkoza.starter.graphs.GraphDataProvider
import com.kkoza.starter.graphs.ItemsDto
import com.kkoza.starter.handles.api.*
import org.joda.time.DateTime

class MeasurementFacade(
        private val handleMeasurementOperation: HandleMeasurementOperation
) {

    fun addHandleMeasurement(handleMeasurementDocument: HandleMeasurementDocument): String {
        return handleMeasurementOperation.addHandleMeasurement(handleMeasurementDocument)
    }

    fun getHandleMeasurements(userId: String, sort: HandleSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?): MeasurementList {
        return handleMeasurementOperation.getHandleMeasurement(userId, sort, offset, limit, alarms, handles)
    }

    fun deleteHandleMeasurementById(id: String) {
        handleMeasurementOperation.deleteHandleMeasurementById(id)
    }

    fun findOneFromAllDevices(userId: String): List<HandleMeasurement> {
        return handleMeasurementOperation.findOneFromEveryHandle(userId)
    }

}