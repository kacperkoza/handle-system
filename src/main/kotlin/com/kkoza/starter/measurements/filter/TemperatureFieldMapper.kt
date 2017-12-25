package com.kkoza.starter.measurements.filter

import com.kkoza.starter.measurements.HandleMeasurementDocument
import com.kkoza.starter.measurements.api.FieldFilter
import com.kkoza.starter.measurements.api.GraphItem
import org.springframework.stereotype.Component

@Component
class TemperatureFieldMapper : GraphFieldMapper {

    override fun shouldApply(fieldFilter: FieldFilter): Boolean = fieldFilter == FieldFilter.TEMPERATURE

    override fun map(handleMeasurements: List<HandleMeasurementDocument>): List<GraphItem> = handleMeasurements.map { GraphItem(it.date, it.temperature.value) }

}