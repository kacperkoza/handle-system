package com.kkoza.starter.measurements

import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.user.UserFacade
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate, smsClient: SmsClient, userFacade: UserFacade): MeasurementFacade {
        val measurementRepository = MeasurementRepository(mongoTemplate)
        val dangerEventNotifier = DangerEventNotifier(smsClient)
        val measurementOperation = MeasurementOperation(measurementRepository, dangerEventNotifier, userFacade)
        return MeasurementFacade(measurementOperation)
    }
}