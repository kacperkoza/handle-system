package com.kkoza.starter.measurements

import com.kkoza.starter.testutil.MeasurementBuilder
import com.kkoza.starter.user.UserDocument

class DangerEventNotifierTest extends MeasurementFacadeSpec {

    def 'should notify about danger when new measurement come with any of alarm set to true'() {
        given:
        def measurement = MeasurementBuilder.create()
                .setAlarm(fire, burglar, frost)
                .setHandleId('handle-id')
                .build()
        userRepository.findUserWithHandle('handle-id') >> new UserDocument(null, '', '', '', '', '', '123456789', [])

        when:
        measurementFacade.add(measurement)

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
