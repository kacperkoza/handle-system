package com.kkoza.starter.graphs.nodemapper

import com.kkoza.starter.graphs.GraphItem
import com.kkoza.starter.graphs.NodeFieldFilter
import com.kkoza.starter.nodes.NodeMeasurementDocument
import org.springframework.stereotype.Component

@Component
class CarbonDioxideNodeMapper : NodeMapper {

    override fun shouldApply(nodeFieldFilter: NodeFieldFilter): Boolean  = nodeFieldFilter == NodeFieldFilter.CARBON

    override fun map(handleMeasurements: List<NodeMeasurementDocument>): List<GraphItem> = handleMeasurements.map { GraphItem(it.date, it.carbonDioxide.value) }

}