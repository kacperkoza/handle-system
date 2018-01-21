package com.kkoza.starter.nodes

import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.nodes.api.NodeFilter
import org.apache.log4j.Logger
import org.joda.time.DateTime
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
        logger.info("Save new node measurement $nodeMeasurementDocument")
        mongoTemplate.save(nodeMeasurementDocument)
        return nodeMeasurementDocument.id!!
    }

    fun getNodeMeasurements(nodes: List<String>, sort: Sort, fieldFilters: List<NodeFilter>?, startDate: DateTime?, endDate: DateTime?): List<NodeMeasurementDocument> {
        logger.info("Get measurement list for nodes = $nodes and sort = $sort")
        val criteria = buildCriteria(nodes, fieldFilters, startDate, endDate)
        return mongoTemplate.find(
                Query(criteria).with(sort),
                NodeMeasurementDocument::class.java)
    }

    private fun buildCriteria(nodes: List<String>, fieldFilters: List<NodeFilter>?, startDate: DateTime?, endDate: DateTime?): Criteria {
        var criteria = whereNodeIdCriteria(nodes)
        fieldFilters?.forEach { criteria = whereFieldCriteria(criteria, it) }
        val startCriteria = whereStartDateCriteria(startDate)
        val endCriteria: Criteria = whereEndDateCriteria(endDate)
        return if (startDate != null && endDate != null) {
            criteria.andOperator(startCriteria, endCriteria)
        } else if (startDate != null) {
            criteria.andOperator(startCriteria)
        } else if (endDate != null) {
            criteria.andOperator(endCriteria)
        } else {
            criteria
        }
    }

    private fun whereNodeIdCriteria(nodes: List<String>): Criteria = Criteria.where(NodeMeasurementDocument.NODE_ID).`in`(nodes)

    private fun whereFieldCriteria(criteria: Criteria, it: NodeFilter) = criteria.and(it.fieldName).`is`(it.value)

    private fun whereStartDateCriteria(dateTime: DateTime?): Criteria = Criteria.where(HandleMeasurementDocument.DATE).gt(dateTime)

    private fun whereEndDateCriteria(endDate: DateTime?): Criteria = Criteria.where(HandleMeasurementDocument.DATE).lt(endDate)

    fun deleteNodeMeasurementById(id: String) {
        logger.info("Delete measurement id = $id")
        mongoTemplate.remove(
                Query(Criteria(NodeMeasurementDocument.ID).`is`(id)),
                NodeMeasurementDocument::class.java)
    }

    fun count(filteredNodes: List<String>, fieldFilters: List<NodeFilter>?, startDate: DateTime?, endDate: DateTime?): Long {
        val criteria = buildCriteria(filteredNodes, fieldFilters, startDate, endDate)
        return mongoTemplate.count(Query.query(criteria), NodeMeasurementDocument::class.java)
    }

    fun findMostRecentNodeMeasurement(userId: String, nodeId: String): NodeMeasurementDocument? {
        logger.info("Find most recent for userId = $userId and nodeId = $nodeId")
        return mongoTemplate.findOne(Query(
                whereNodeId(nodeId)),
                NodeMeasurementDocument::class.java)
    }

    private fun whereNodeId(handleId: String) = Criteria(NodeMeasurementDocument.NODE_ID).`is`(handleId)

}