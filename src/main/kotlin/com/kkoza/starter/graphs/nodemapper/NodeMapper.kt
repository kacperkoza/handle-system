package com.kkoza.starter.graphs.nodemapper

import com.kkoza.starter.graphs.GraphItem
import com.kkoza.starter.graphs.NodeFieldFilter
import com.kkoza.starter.nodes.NodeMeasurementDocument

interface NodeMapper {

    fun shouldApply(nodeFieldFilter: NodeFieldFilter): Boolean

    fun map(handleMeasurements: List<NodeMeasurementDocument>): List<GraphItem>

}