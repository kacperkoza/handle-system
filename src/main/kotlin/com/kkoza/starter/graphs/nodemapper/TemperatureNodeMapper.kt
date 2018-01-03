package com.kkoza.starter.graphs.nodemapper

import com.kkoza.starter.graphs.GraphItem
import com.kkoza.starter.graphs.NodeFieldFilter
import com.kkoza.starter.graphs.NodeFieldFilter.TEMPERATURE
import com.kkoza.starter.nodes.NodeMeasurementDocument
import org.springframework.stereotype.Component

@Component
class TemperatureNodeMapper : NodeMapper {

    override fun shouldApply(nodeFieldFilter: NodeFieldFilter): Boolean = nodeFieldFilter == TEMPERATURE

    override fun map(handleMeasurements: List<NodeMeasurementDocument>): List<GraphItem> {
        return handleMeasurements.map { GraphItem(it.date, it.temperature.value) }
    }

}