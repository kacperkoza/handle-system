package com.kkoza.starter.measurements

import org.joda.time.DateTime
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class GraphRepository(
        private val mongoTemplate: MongoTemplate
) {

    fun getMeasurementsForGraph(startDate: DateTime, endDate: DateTime, handleId: String): List<HandleMeasurementDocument> {
        return mongoTemplate.find(
                Query(Criteria().andOperator(
                        whereHandleIdCriteria(handleId),
                        whereStartDateCriteria(startDate),
                        whereEndDateCriteria(endDate))),
                HandleMeasurementDocument::class.java)
    }

    private fun whereEndDateCriteria(endDate: DateTime) = Criteria.where(HandleMeasurementDocument.DATE).lt(endDate)

    private fun whereStartDateCriteria(startDate: DateTime) =  Criteria.where(HandleMeasurementDocument.DATE).gt(startDate)

    private fun whereHandleIdCriteria(handleId: String) = Criteria.where(HandleMeasurementDocument.HANDLE_ID).`is`(handleId)
}