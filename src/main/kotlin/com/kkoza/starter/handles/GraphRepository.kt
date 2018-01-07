package com.kkoza.starter.handles

import com.kkoza.starter.nodes.NodeMeasurementDocument
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class GraphRepository(
        private val mongoTemplate: MongoTemplate
) {

    fun getMeasurementsForGraph(startDate: DateTime, endDate: DateTime, handleId: String): List<HandleMeasurementDocument> {
        return getMeasurements(startDate, endDate, handleId, HandleMeasurementDocument::class.java)
    }

    fun getNodeMeasurements(start: DateTime, end: DateTime, nodeId: String): List<NodeMeasurementDocument> {
        return getMeasurements(start, end, nodeId, NodeMeasurementDocument::class.java)
    }

    @SuppressWarnings("unchecked")
    private fun <T> getMeasurements(startDate: DateTime, endDate: DateTime, deviceId: String, clazz: Class<T>): List<T> {
        return if (clazz == NodeMeasurementDocument::class.java) {
            mongoTemplate.find(
                    queryWithStartAndEndDateCriteria(startDate, endDate, NodeMeasurementDocument.DATE)
                            .addCriteria(whereNodeIdCriteria(deviceId)),
                    clazz) as List<out T>
        } else {
            mongoTemplate.find(
                    queryWithStartAndEndDateCriteria(startDate, endDate, HandleMeasurementDocument.DATE)
                            .addCriteria(whereHandleIdCriteria(deviceId)),
                    clazz)
        }
    }

    private fun queryWithStartAndEndDateCriteria(startDate: DateTime, endDate: DateTime, dateFieldName: String): Query {
        return Query(Criteria().andOperator(
                whereStartDateCriteria(startDate, dateFieldName),
                whereEndDateCriteria(endDate, dateFieldName)))
    }

    private fun whereEndDateCriteria(endDate: DateTime, fieldName: String) = Criteria.where(fieldName).lt(endDate)

    private fun whereStartDateCriteria(startDate: DateTime, fieldName: String) = Criteria.where(fieldName).gt(startDate)

    private fun whereHandleIdCriteria(handleId: String) = Criteria.where(HandleMeasurementDocument.HANDLE_ID).`is`(handleId)

    private fun whereNodeIdCriteria(nodeId: String) = Criteria.where(NodeMeasurementDocument.NODE_ID).`is`(nodeId)

}
