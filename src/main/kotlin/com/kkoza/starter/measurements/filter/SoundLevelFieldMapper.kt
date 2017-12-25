package com.kkoza.starter.measurements.filter

import com.kkoza.starter.measurements.HandleMeasurementDocument
import com.kkoza.starter.measurements.api.FieldFilter
import com.kkoza.starter.measurements.api.GraphItem
import org.springframework.stereotype.Component

@Component
class SoundLevelFieldMapper : GraphFieldMapper {

    override fun shouldApply(fieldFilter: FieldFilter): Boolean = fieldFilter == FieldFilter.SOUND_LEVEL

    override fun map(handleMeasurements: List<HandleMeasurementDocument>): List<GraphItem> = handleMeasurements.map { GraphItem(it.date, it.soundLevel.value) }

}