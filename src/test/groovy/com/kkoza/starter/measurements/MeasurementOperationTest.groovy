package com.kkoza.starter.measurements

import com.github.fakemongo.Fongo
import com.kkoza.starter.handles.HandleConfiguration
import com.kkoza.starter.handles.HandleDocument
import com.kkoza.starter.handles.HandleFacade
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.testutil.MeasurementBuilder
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.user.UserFacadeConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

class MeasurementOperationTest extends Specification {

    MongoTemplate mongoTemplate = new MongoTemplate(new Fongo('test').getMongo(), 'test')

    SmsClient smsClient = Mock(SmsClient)

    MeasurementOperation measurementOperation

    def setup() {
        MeasurementRepository measurementRepository = new MeasurementRepository(mongoTemplate)
        DangerEventNotifier dangerEventNotifier = new DangerEventNotifier(smsClient)
        HandleFacade handleFacade = new HandleConfiguration().handleFacade(mongoTemplate)
        UserFacade userFacade = new UserFacadeConfiguration().userFacade(mongoTemplate)
        measurementOperation = new MeasurementOperation(measurementRepository, dangerEventNotifier, handleFacade, userFacade)
    }

    def 'should notify about danger when new measurement come with any of alarm set to true'() {
        given:
        mongoTemplate.save(new UserDocument('user-id', 'any', 'any', '123456789'))
        mongoTemplate.save(new HandleDocument('handleAlarmFilterEx-id', 'name', 'user-id'))
        def measurement = MeasurementBuilder.create()
                .setAlarm(fire, burglar, frost)
                .setHandleId('handleAlarmFilterEx-id')
                .build()

        when:
        measurementOperation.add(measurement)

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
