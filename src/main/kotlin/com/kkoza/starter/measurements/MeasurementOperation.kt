package com.kkoza.starter.measurements

import org.springframework.data.mongodb.core.MongoTemplate


class MeasurementOperation(
        private val mongoTemplate: MongoTemplate
) {
    fun add(measurement: Measurement) {
        mongoTemplate.save(measurement)
    }

    fun findAll(): List<Measurement> {
        return mongoTemplate.findAll(Measurement::class.java)
    }

}