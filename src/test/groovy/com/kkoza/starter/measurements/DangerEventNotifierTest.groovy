package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementDto
import com.kkoza.starter.util.MeasurementBuilder
import org.joda.time.DateTime
import spock.lang.Shared
import spock.lang.Unroll

class DangerEventNotifierTest extends MeasurementFacadeSpec {

    def 'should notify about danger when new measurement come with any of alarm set to true'() {
        given:
        def measurement = MeasurementBuilder.create().setAlarm(fire, burglar, frost).build()

        when:
        measurementFacade.add(measurement)

        then:
        numberOfInteractions * smsClient.sendSMS(_, _)

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
