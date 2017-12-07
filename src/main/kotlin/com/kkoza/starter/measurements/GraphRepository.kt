package com.kkoza.starter.measurements

import org.joda.time.DateTime
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class GraphRepository(
        private val mongoTemplate: MongoTemplate
) {

    fun getMeasurementsForGraph(startDate: DateTime, endDate: DateTime, handleId: String): List<MeasurementDocument> {
        return mongoTemplate.find(
                Query(Criteria().andOperator(
                        whereHandleIdCriteria(handleId),
                        whereStartDateCriteria(startDate),
                        whereEndDateCriteria(endDate))),
                MeasurementDocument::class.java)


    }

    private fun whereEndDateCriteria(endDate: DateTime) = Criteria.where(MeasurementDocument.DATE).lt(endDate)

    private fun whereStartDateCriteria(startDate: DateTime) =  Criteria.where(MeasurementDocument.DATE).gt(startDate)

    private fun whereHandleIdCriteria(handleId: String) = Criteria.where(MeasurementDocument.HANDLE_ID).`is`(handleId)
}