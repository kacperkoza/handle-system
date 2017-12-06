package com.kkoza.starter.handles

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class HandleConfiguration {

    @Bean
    fun handleFacade(mongoTemplate: MongoTemplate): HandleFacade {
        val handleRepository = HandleRepository(mongoTemplate)
        val handleOperation = HandleOperation(handleRepository)
        return HandleFacade(handleRepository, handleOperation)
    }
}