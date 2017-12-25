package com.kkoza.starter.measurements

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.measurements.fieldmapper.GraphFieldMapper
import com.kkoza.starter.user.UserFacade
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate, smsClient: SmsClient, userFacade: UserFacade, deviceFacade: DeviceFacade, mappers: List<GraphFieldMapper>): MeasurementFacade {
        val measurementRepository = MeasurementRepository(mongoTemplate)
        val dangerEventNotifier = DangerEventNotifier(smsClient)
        val measurementOperation = MeasurementOperation(measurementRepository, dangerEventNotifier, deviceFacade, userFacade)
        val graphRepository = GraphRepository(mongoTemplate)
        val graphDataProvider = GraphDataProvider(graphRepository, mappers)
        return MeasurementFacade(measurementOperation, graphDataProvider)
    }
}