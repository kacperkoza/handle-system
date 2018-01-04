package com.kkoza.starter.graphs.handlemapper

import com.kkoza.starter.graphs.GraphItem
import com.kkoza.starter.graphs.HandleFieldFilter
import com.kkoza.starter.handles.HandleMeasurementDocument
import org.springframework.stereotype.Component

@Component
class SoundLevelFieldMapper : HandleMapper {

    override fun shouldApply(handleFieldFilter: HandleFieldFilter): Boolean {
        return handleFieldFilter == HandleFieldFilter.SOUND_LEVEL
    }

    override fun map(handleMeasurements: List<HandleMeasurementDocument>): List<GraphItem> {
        return handleMeasurements.map { GraphItem(it.date, it.soundLevel) }
    }

}