package com.kkoza.starter.handles

import com.kkoza.starter.handles.api.AlarmFilter
import org.apache.log4j.Logger
import org.joda.time.DateTime
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Repository
class HandleMeasurementRepository(private val mongoTemplate: MongoTemplate) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun add(handleMeasurementDocument: HandleMeasurementDocument): String {
        logger.info("Save new handleMeasurementDocument $handleMeasurementDocument")
        mongoTemplate.save(handleMeasurementDocument)
        return handleMeasurementDocument.id!!
    }

    fun get(handles: List<String>, sort: Sort, alarms: List<AlarmFilter>?, offset: Int, limit: Int, startDate: DateTime?, endDate: DateTime?): List<HandleMeasurementDocument> {
        logger.info("Get measurement list for $handles and $sort")
        val criteria = buildCriteria(handles, alarms, startDate, endDate)
        return mongoTemplate.find(
                Query(criteria)
                        .with(sort)
                        .skip(offset)
                        .limit(limit),
                HandleMeasurementDocument::class.java)
    }

    fun count(handles: List<String>, sort: Sort, alarms: List<AlarmFilter>?, startDate: DateTime?, endDate: DateTime?): Long {
        logger.info({ "Count documents for handles = $handles, sort = $sort, alarms = $alarms" })
        val criteria = buildCriteria(handles, alarms, startDate, endDate)
        return mongoTemplate.count(
                Query(criteria).with(sort),
                HandleMeasurementDocument::class.java)
    }

    private fun buildCriteria(handles: List<String>, alarms: List<AlarmFilter>?, startDate: DateTime?, endDate: DateTime?): Criteria {
        var criteria = whereHandleIdsCriteria(handles)
        alarms?.forEach { criteria = whereAlarmCriteria(criteria, it) }
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

    private fun whereHandleIdsCriteria(handles: List<String>): Criteria = Criteria.where(HandleMeasurementDocument.HANDLE_ID).`in`(handles)

    private fun whereAlarmCriteria(criteria: Criteria, it: AlarmFilter) = criteria.and(it.fieldName).`is`(it.value)

    private fun whereStartDateCriteria(dateTime: DateTime?): Criteria = Criteria.where(HandleMeasurementDocument.DATE).gt(dateTime)

    private fun whereEndDateCriteria(endDate: DateTime?): Criteria = Criteria.where(HandleMeasurementDocument.DATE).lt(endDate)

    fun delete(id: String) {
        logger.info("Delete measurement id = $id")
        mongoTemplate.remove(
                Query(Criteria(HandleMeasurementDocument.ID).`is`(id)),
                HandleMeasurementDocument::class.java)
    }

    fun findMostRecent(userId: String, handleId: String): HandleMeasurementDocument? {
        logger.info("Find most recent for userId = $userId and deviceId = $handleId")
        return mongoTemplate.findOne(Query(
                whereHandleIdCriteria(handleId)),
                HandleMeasurementDocument::class.java)
    }

    private fun whereHandleIdCriteria(handleId: String) = Criteria(HandleMeasurementDocument.HANDLE_ID).`is`(handleId)

}