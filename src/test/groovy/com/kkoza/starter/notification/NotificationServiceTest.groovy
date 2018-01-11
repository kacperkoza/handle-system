package com.kkoza.starter.notification

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.settings.SettingsDocument
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.HandleBuilder
import com.kkoza.starter.testutil.UserBuilder
import com.kkoza.starter.user.UserFacade
import spock.lang.Specification

class NotificationServiceTest extends Specification {

    NotificationService notificationService

    SmsClient smsClient = Mock(SmsClient.class)
    DeviceFacade deviceFacade = Stub(DeviceFacade.class)
    UserFacade userFacade = Stub(UserFacade.class)
    SettingsService settingsService = Stub(SettingsService.class)

    def setup() {
        notificationService = new NotificationService(
                smsClient,
                new NotifierMessageFactory(),
                deviceFacade,
                userFacade,
                settingsService)
    }

    def 'should send SMS when any of alarm is set to true'() {
        given:
        def measurement = HandleBuilder.create().setAlarm(fire, burglar, frost).setHandleId('handle-id').build()
        deviceFacade.findById('handle-id') >> DeviceBuilder.create().setUserId('user-id').setId('handle-id').buildDocument()
        userFacade.findUserById('user-id') >> UserBuilder.create('user-id').setPhoneNumber('123456789').buildDocument()
        settingsService.findByUserId('user-id') >> new SettingsDocument('user-id', 0, false)

        when:
        notificationService.notifyIfNecessary(measurement)

        then:
        sendTimes * smsClient.sendSMS('123456789', _)

        where:
        fire  | burglar | frost || sendTimes
        false | false   | false || 0
        true  | false   | false || 1
        true  | true    | false || 2
        true  | true    | true  || 3
    }

}
