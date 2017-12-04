package com.kkoza.starter.measurements

import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Repository
class MeasurementRepository(private val mongoTemplate: MongoTemplate) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun add(measurementDocument: MeasurementDocument): String {
        logger.info("Save new measurementDocument $measurementDocument")
        mongoTemplate.save(measurementDocument)
        return measurementDocument.id!!
    }

    fun get(handles: List<String>, sort: Sort): List<MeasurementDocument> {
        logger.info("Get measurement list for $handles and $sort")
        return mongoTemplate.find(
                Query(Criteria(MeasurementDocument.HANDLE_ID).`in`(handles)).with(sort),
                MeasurementDocument::class.java)
    }

    fun delete(id: String) {
        logger.info("Delete measurement id = $id")
        mongoTemplate.remove(
                Query(Criteria(MeasurementDocument.ID).`is`(id)),
                MeasurementDocument::class.java)
    }

}