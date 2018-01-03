package com.kkoza.starter.graphs

import org.joda.time.DateTime
import org.springframework.stereotype.Service

@Service
class GraphFacade(
        private val graphDataProvider: GraphDataProvider

) {
    fun getGraphDataFromHandle(startDate: DateTime?, endDate: DateTime?, handleFieldFilter: HandleFieldFilter, handleId: String): ItemsDto? {
        return graphDataProvider.getGraphDataFromHandle(startDate, endDate, handleFieldFilter, handleId)
    }

    fun getGraphDataFromNode(startDate: DateTime?, endDate: DateTime?, nodeFieldFilter: NodeFieldFilter, nodeId: String): ItemsDto? {
        return graphDataProvider.getGraphDataFromNode(startDate, endDate, nodeFieldFilter, nodeId)
    }

}