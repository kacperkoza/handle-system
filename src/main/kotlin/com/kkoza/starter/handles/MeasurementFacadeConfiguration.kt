package com.kkoza.starter.handles

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.graphs.GraphDataProvider
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.graphs.handlemapper.HandleMapper
import com.kkoza.starter.graphs.nodemapper.NodeMapper
import com.kkoza.starter.user.UserFacade
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate, smsClient: SmsClient, userFacade: UserFacade, deviceFacade: DeviceFacade, mappers: List<HandleMapper>, nodeMappers: List<NodeMapper>): MeasurementFacade {
        val measurementRepository = HandleMeasurementRepository(mongoTemplate)
        val dangerEventNotifier = DangerEventNotifier(smsClient)
        val measurementOperation = HandleMeasurementOperation(measurementRepository, dangerEventNotifier, deviceFacade, userFacade)
        return MeasurementFacade(measurementOperation)
    }
}