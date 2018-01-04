package com.kkoza.starter.graphs.nodemapper

import com.kkoza.starter.graphs.GraphItem
import com.kkoza.starter.graphs.NodeFieldFilter
import com.kkoza.starter.nodes.NodeMeasurementDocument
import org.springframework.stereotype.Component

@Component
class HumidityNodeMapper : NodeMapper {

    override fun shouldApply(nodeFieldFilter: NodeFieldFilter): Boolean {
        return nodeFieldFilter == NodeFieldFilter.HUMIDITY
    }

    override fun map(handleMeasurements: List<NodeMeasurementDocument>): List<GraphItem> {
        return handleMeasurements.map { GraphItem(it.date, it.humidity) }
    }

}