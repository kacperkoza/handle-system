package com.kkoza.starter.measurements

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate): MeasurementFacade {
        val operation = MeasurementOperation(mongoTemplate)
        return MeasurementFacade(operation)
    }
}