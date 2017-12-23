package com.kkoza.starter.devices

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class HandleConfiguration {

    @Bean
    fun handleFacade(mongoTemplate: MongoTemplate): DeviceFacade {
        val handleRepository = HandleRepository(mongoTemplate)
        val handleOperation = HandleOperation(handleRepository)
        val nodeRepository = NodeRepository(mongoTemplate)
        val nodeOperation = NodeOperation(nodeRepository)
        return DeviceFacade(handleRepository, handleOperation, nodeOperation, nodeRepository)
    }
}