package com.kkoza.starter.measurements.filter

import com.kkoza.starter.measurements.MeasurementDocument
import com.kkoza.starter.measurements.api.FieldFilter
import com.kkoza.starter.measurements.api.GraphItem

interface GraphFieldMapper {

    fun shouldApply(fieldFilter: FieldFilter): Boolean

    fun map(measurements: List<MeasurementDocument>): List<GraphItem>

}