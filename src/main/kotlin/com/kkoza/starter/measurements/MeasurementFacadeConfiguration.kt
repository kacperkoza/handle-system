package com.kkoza.starter.measurements

import com.kkoza.starter.infrastructure.smsclient.SmsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate, smsClient: SmsClient): MeasurementFacade {
        val measurementRepository = MeasurementRepository(mongoTemplate)
        val dangerEventNotifier = DangerEventNotifier(smsClient)
        val measurementOperation = MeasurementOperation(measurementRepository, dangerEventNotifier)
        return MeasurementFacade(measurementOperation)
    }
}