package com.kkoza.starter.nodes

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.DeviceRepository
import com.kkoza.starter.devices.HandleOperation
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.notification.NotificationCoordinator
import com.kkoza.starter.notification.NotifierMessageFactory
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.user.UserFacade
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class NodeFacadeConfiguration {

    @Bean
    fun nodeFacade(mongoTemplate: MongoTemplate,
                   smsClient: SmsClient,
                   deviceFacade: DeviceFacade,
                   userFacade: UserFacade,
                   settingsService: SettingsService
                   ): NodeFacade {
        val nodeMeasurementRepository = NodeMeasurementRepository(mongoTemplate)
        val notificationCoordinator = NotificationCoordinator(smsClient, NotifierMessageFactory(), deviceFacade, userFacade, settingsService)
        val nodeMeasurementOperation = NodeMeasurementOperation(nodeMeasurementRepository, deviceFacade, notificationCoordinator)
        return NodeFacade(nodeMeasurementOperation)
    }

}