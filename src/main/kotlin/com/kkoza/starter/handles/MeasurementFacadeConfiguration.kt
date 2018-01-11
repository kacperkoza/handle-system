package com.kkoza.starter.handles

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.graphs.handlemapper.HandleMapper
import com.kkoza.starter.graphs.nodemapper.NodeMapper
import com.kkoza.starter.notification.NotificationCoordinator
import com.kkoza.starter.notification.NotifierMessageFactory
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.user.UserFacade
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MeasurementFacadeConfiguration {

    @Bean
    fun measurementFacade(mongoTemplate: MongoTemplate,
                          smsClient: SmsClient,
                          userFacade: UserFacade,
                          deviceFacade: DeviceFacade,
                          mappers: List<HandleMapper>,
                          nodeMappers: List<NodeMapper>,
                          settingsService: SettingsService): MeasurementFacade {
        val measurementRepository = HandleMeasurementRepository(mongoTemplate)
        val notificationService = NotificationCoordinator(smsClient, NotifierMessageFactory(), deviceFacade, userFacade, settingsService)
        val measurementOperation = HandleMeasurementOperation(measurementRepository, deviceFacade, notificationService)
        return MeasurementFacade(measurementOperation)
    }
}