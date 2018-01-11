package com.kkoza.starter.notification

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.settings.SettingsDocument
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.HandleBuilder
import com.kkoza.starter.testutil.NodeBuilder
import com.kkoza.starter.testutil.UserBuilder
import com.kkoza.starter.user.UserFacade
import spock.lang.Specification
import spock.lang.Unroll

class NotificationCoordinatorTest extends Specification {

    NotificationCoordinator notificationService

    SmsClient smsClient = Mock(SmsClient.class)
    DeviceFacade deviceFacade = Stub(DeviceFacade.class)
    UserFacade userFacade = Stub(UserFacade.class)
    SettingsService settingsService = Stub(SettingsService.class)

    def setup() {
        notificationService = new NotificationCoordinator(
                smsClient,
                new NotifierMessageFactory(),
                deviceFacade,
                userFacade,
                settingsService)
        stubDeviceFacade('device-id')
        stubUserFacade('123456789')
    }

    def 'should send SMS when any of alarm is set to true'() {
        given:
        def handleMeasurement = HandleBuilder.create().setAlarm(fire, burglar, frost).setHandleId('device-id').build()
        stubSettingsService(-1000, false)

        when:
        notificationService.notifyIfNecessary(handleMeasurement)

        then:
        sendTimes * smsClient.sendSMS('123456789', _)

        where:
        fire  | burglar | frost || sendTimes
        false | false   | false || 0
        true  | false   | false || 1
        true  | true    | false || 2
        true  | true    | true  || 2
    }

    def 'should send alarm when handle temperature is lower than in user\'s settings'() {
        given:
        def handleMeasurement = HandleBuilder.create().setAlarm(false, false, false).setHandleId('device-id')
                .setTemperature(10.0)
                .build()
        stubSettingsService(minTemperatureToSendNotification, false)

        when:
        notificationService.notifyIfNecessary(handleMeasurement)

        then:
        sendTimes * smsClient.sendSMS('123456789', _)

        where:
        minTemperatureToSendNotification || sendTimes
        9.0d                             || 0
        9.9d                             || 0
        10.1d                            || 1
        15d                              || 1
    }

    def 'should send notification when alarm is enabled and node measurement detected motion'() {
        given:
        def nodeMeasurement = NodeBuilder.create().setMotion(true).setNodeId('device-id').build()
        stubSettingsService(0, motionDetected)

        when:
        notificationService.notifyIfNecessary(nodeMeasurement)

        then:
        sendTimes * smsClient.sendSMS('123456789', _)

        where:
        motionDetected << [true, false]
        sendTimes << [1, 0]
    }

    def stubSettingsService(double minTemperature, boolean motionDetected) {
        settingsService.findByUserId('user-id') >> new SettingsDocument('user-id', minTemperature, motionDetected)
    }

    def stubUserFacade(String phoneNumber) {
        userFacade.findUserById('user-id') >> UserBuilder.create('user-id').setPhoneNumber(phoneNumber).buildDocument()
    }

    def stubDeviceFacade(String handleId) {
        deviceFacade.findById(handleId) >> DeviceBuilder.create().setUserId('user-id').setId(handleId).buildDocument()
    }

}
