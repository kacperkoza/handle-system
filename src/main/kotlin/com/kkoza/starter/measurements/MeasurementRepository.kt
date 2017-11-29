package com.kkoza.starter.measurements

import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.dto.UserDto
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class MeasurementRepository(private val mongoTemplate: MongoTemplate) {

    fun add(measurement: Measurement): String {
        mongoTemplate.save(measurement)
        return measurement.id!!
    }

    fun get(handles: List<String>, sort: Sort): List<Measurement> {
        return mongoTemplate.find(
                Query(Criteria(Measurement.HANDLE_ID).`in`(handles)).with(sort),
                Measurement::class.java)
    }

    fun deleteById(id: String) {
        mongoTemplate.remove(
                Query(Criteria(Measurement.ID).`is`(id)),
                Measurement::class.java)
    }

}