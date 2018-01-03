package com.kkoza.starter.nodes

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.DeviceRepository
import com.kkoza.starter.devices.HandleOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class NodeFacadeConfiguration {

    @Bean
    fun nodeFacade(mongoTemplate: MongoTemplate): NodeFacade {
        val nodeMeasurementRepository = NodeMeasurementRepository(mongoTemplate)
        val deviceRepository = DeviceRepository(mongoTemplate)
        val handleOperation = HandleOperation(deviceRepository)
        val deviceFacade = DeviceFacade(deviceRepository, handleOperation)
        val nodeMeasurementOperation = NodeMeasurementOperation(nodeMeasurementRepository, deviceFacade)
        return NodeFacade(nodeMeasurementOperation)
    }

}