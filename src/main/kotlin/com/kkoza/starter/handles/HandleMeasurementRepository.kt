package com.kkoza.starter.handles

import com.kkoza.starter.handles.api.AlarmFilter
import org.apache.log4j.Logger
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

    fun get(handles: List<String>, sort: Sort, alarms: List<AlarmFilter>?): List<HandleMeasurementDocument> {
        logger.info("Get measurement list for $handles and $sort")
        var criteria = whereHandleIdCriteria(handles)
        alarms?.forEach { criteria = whereAlarmCriteria(criteria, it) }
        return mongoTemplate.find(
                Query(criteria).with(sort),
                HandleMeasurementDocument::class.java)
    }

    private fun whereAlarmCriteria(criteria: Criteria, it: AlarmFilter) = criteria.and(it.fieldName).`is`(it.value)

    private fun whereHandleIdCriteria(handles: List<String>): Criteria = Criteria.where(HandleMeasurementDocument.HANDLE_ID).`in`(handles)

    fun delete(id: String) {
        logger.info("Delete measurement id = $id")
        mongoTemplate.remove(
                Query(Criteria(HandleMeasurementDocument.ID).`is`(id)),
                HandleMeasurementDocument::class.java)
    }

    fun findMostRecent(userId: String, handleId: String): HandleMeasurementDocument? {
        logger.info("Find most recent for userId = $userId and handleId = $handleId")
        return mongoTemplate.findOne(Query(
                whereHandleId(handleId)),
                HandleMeasurementDocument::class.java)
    }

    private fun whereHandleId(handleId: String) = Criteria(HandleMeasurementDocument.HANDLE_ID).`is`(handleId)

}