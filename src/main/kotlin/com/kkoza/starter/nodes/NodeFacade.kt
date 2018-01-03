package com.kkoza.starter.nodes

import com.kkoza.starter.graphs.HandleFieldFilter
import com.kkoza.starter.graphs.ItemsDto
import com.kkoza.starter.nodes.api.NodeFilter
import org.joda.time.DateTime

class NodeFacade(
        private val nodeMeasurementOperation: NodeMeasurementOperation
) {

    fun addNodeMeasurement(nodeMeasurementDocument: NodeMeasurementDocument): String {
        return nodeMeasurementOperation.addNodeMeasurement(nodeMeasurementDocument)
    }

    fun getNodeMeasurement(userId: String, sort: NodeSortType, offset: Int?, limit: Int?, nodes: List<String>?, fieldFilters: List<NodeFilter>): NodeMeasurementList {
        return nodeMeasurementOperation.getNodeMeasurement(userId, sort, offset, limit, nodes, fieldFilters)
    }

    fun deleteNodeMeasurementById(id: String) {
        nodeMeasurementOperation.deleteNodeMeasurementById(id)
    }

    fun getGraphDataFromNode(startDate: DateTime?, endDate: DateTime?, handleFieldName: HandleFieldFilter, nodeId: String): ItemsDto {
//        return graphDataProvider.get(startDate, endDate, handleFieldName, handleId)
        return ItemsDto(emptyList())
    }

    fun findOneFromAllNodes(userId: String): List<NodeMeasurement> {
        return nodeMeasurementOperation.findOneFromEveryUserNode(userId)
    }

}