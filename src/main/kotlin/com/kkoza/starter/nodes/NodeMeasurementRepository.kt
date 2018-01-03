package com.kkoza.starter.nodes

import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.nodes.api.NodeFilter
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.invoke.MethodHandles

class NodeMeasurementRepository(
        private val mongoTemplate: MongoTemplate
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun addNodeMeasurement(nodeMeasurementDocument: NodeMeasurementDocument): String {
        logger.info("Save new node measurements $nodeMeasurementDocument")
        mongoTemplate.save(nodeMeasurementDocument)
        return nodeMeasurementDocument.id!!
    }

    fun getNodeMeasurements(nodes: List<String>, sort: Sort, fieldFilters: List<NodeFilter>): List<NodeMeasurementDocument> {
        logger.info("Get measurement list for nodes = $nodes and sort = $sort")
        var criteria = whereNodeIdCriteria(nodes)
        fieldFilters?.forEach { criteria = whereFieldCriteria(criteria, it) }
        return mongoTemplate.find(
                Query(criteria).with(sort),
                NodeMeasurementDocument::class.java)
    }

    private fun whereNodeIdCriteria(nodes: List<String>): Criteria = Criteria.where(NodeMeasurementDocument.NODE_ID).`in`(nodes)

    private fun whereFieldCriteria(criteria: Criteria, it: NodeFilter) = criteria.and(it.fieldName).`is`(it.value)

    fun deleteNodeMeasurementById(id: String) {
        logger.info("Delete measurement id = $id")
        mongoTemplate.remove(
                Query(Criteria(NodeMeasurementDocument.ID).`is`(id)),
                NodeMeasurementDocument::class.java)
    }

    fun findMostRecentNodeMeasurement(userId: String, handleId: String): NodeMeasurementDocument? {
        logger.info("Find most recent for userId = $userId and handleId = $handleId")
        return mongoTemplate.findOne(Query(
                whereNodeId(handleId)),
                NodeMeasurementDocument::class.java)
    }

    private fun whereNodeId(handleId: String) = Criteria(HandleMeasurementDocument.HANDLE_ID).`is`(handleId)

}