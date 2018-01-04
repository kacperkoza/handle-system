package com.kkoza.starter.handles

import com.github.fakemongo.Fongo
import com.kkoza.starter.devices.DeviceConfiguration
import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.DeviceType
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.testutil.HandleBuilder
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.user.UserFacadeConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

class HandleHandleMeasurementOperationTest extends Specification {

    MongoTemplate mongoTemplate = new MongoTemplate(new Fongo('test').getMongo(), 'test')

    SmsClient smsClient = Mock(SmsClient)

    HandleMeasurementOperation measurementOperation

    def setup() {
        HandleMeasurementRepository measurementRepository = new HandleMeasurementRepository(mongoTemplate)
        DangerEventNotifier dangerEventNotifier = new DangerEventNotifier(smsClient)
        DeviceFacade handleFacade = new DeviceConfiguration().handleFacade(mongoTemplate)
        UserFacade userFacade = new UserFacadeConfiguration().userFacade(mongoTemplate)
        measurementOperation = new HandleMeasurementOperation(measurementRepository, dangerEventNotifier, handleFacade, userFacade)
    }

    def 'should notify about danger when new measurement come with any of alarm set to true'() {
        given:
        mongoTemplate.save(new UserDocument('user-id', 'any', 'any', '123456789'))
        mongoTemplate.save(new DeviceDocument('node-id', 'name', 'user-id', DeviceType.HANDLE))
        def measurement = HandleBuilder.create()
                .setAlarm(fire, burglar, frost)
                .setHandleId('node-id')
                .build()

        when:
        measurementOperation.addHandleMeasurement(measurement)

        then:
        numberOfInteractions * smsClient.sendSMS('123456789', _)

        where:
        fire  | burglar | frost || numberOfInteractions
        false | false   | false || 0
        true  | false   | false || 1
        false | true    | false || 1
        true  | true    | false || 2
        false | false   | true  || 1
        true  | false   | true  || 2
        true  | true    | false || 2
        true  | true    | true  || 3

    }
}
