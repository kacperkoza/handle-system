package com.kkoza.starter.measurements

import com.kkoza.starter.handles.HandleFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate, smsClient: SmsClient, userFacade: UserFacade, handleFacade: HandleFacade): MeasurementFacade {
        val measurementRepository = MeasurementRepository(mongoTemplate)
        val dangerEventNotifier = DangerEventNotifier(smsClient)
        val measurementOperation = MeasurementOperation(measurementRepository, dangerEventNotifier, handleFacade, userFacade)
        return MeasurementFacade(measurementOperation)
    }
}