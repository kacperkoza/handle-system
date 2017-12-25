package com.kkoza.starter.devices

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class DeviceConfiguration {

    @Bean
    fun handleFacade(mongoTemplate: MongoTemplate): DeviceFacade {
        val handleRepository = DeviceRepository(mongoTemplate)
        val handleOperation = HandleOperation(handleRepository)
        return DeviceFacade(handleRepository, handleOperation)
    }
}