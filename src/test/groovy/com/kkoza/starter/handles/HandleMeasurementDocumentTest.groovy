package com.kkoza.starter.handles

import org.joda.time.DateTime
import spock.lang.Specification

class HandleMeasurementDocumentTest extends Specification {


    def "should throw exception for invalid measurement data"() {
        when:
        new HandleMeasurementDocument('any', DateTime.now(), 'any', HandlePosition.CLOSED, temperature, new Alarm(true, true, true),
                soundLevel, 0)

        then:
        thrown(InvalidHandleMeasurementException)

        where:
        temperature | soundLevel
        -100        | 0
        -21         | 0
        61          | 0
        0           | -300
        0           | -201
        0           | 101
    }


    def "should not throw exception for valid measurement data"() {
        when:
        new HandleMeasurementDocument('any', DateTime.now(), 'any', HandlePosition.CLOSED, temperature, new Alarm(true, true, true),
                soundLevel, 0)

        then:
        noExceptionThrown()

        where:
        temperature | soundLevel
        -20         | 0
        60          | 0
        0           | -200
        0           | 100
    }
}
