package com.kkoza.starter.handles

import com.kkoza.starter.handles.api.AlarmFilter
import com.kkoza.starter.handles.api.HandleSortType
import com.kkoza.starter.handles.dto.HandleMeasurement
import com.kkoza.starter.handles.dto.MeasurementList
import org.joda.time.DateTime

class MeasurementFacade(
        private val handleMeasurementOperation: HandleMeasurementOperation
) {

    fun addHandleMeasurement(handleMeasurementDocument: HandleMeasurementDocument): String {
        return handleMeasurementOperation.addHandleMeasurement(handleMeasurementDocument)
    }

    fun getHandleMeasurements(userId: String, sort: HandleSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?, startDate: DateTime?, endDate: DateTime?): MeasurementList {
        return handleMeasurementOperation.getHandleMeasurement(userId, sort, offset, limit, alarms, handles, startDate, endDate)
    }

    fun deleteHandleMeasurementById(id: String) {
        handleMeasurementOperation.deleteHandleMeasurementById(id)
    }

    fun findOneFromAllDevices(userId: String): List<HandleMeasurement> {
        return handleMeasurementOperation.findOneFromEveryHandle(userId)
    }

}