package com.kkoza.starter.graphs.handlemapper

import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.graphs.HandleFieldFilter
import com.kkoza.starter.graphs.GraphItem

interface HandleMapper {

    fun shouldApply(handleFieldFilter: HandleFieldFilter): Boolean

    fun map(handleMeasurements: List<HandleMeasurementDocument>): List<GraphItem>

}