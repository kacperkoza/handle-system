package com.kkoza.starter.nodes

import org.joda.time.DateTime
import spock.lang.Specification

class NodeMeasurementDocumentTest extends Specification {


    def 'should throw exception for wrong measurement data'() {
        when:
        new NodeMeasurementDocument('id', DateTime.now(), 'node-id', temperature, humidity, lightIntensity, false, carbonDioxide)

        then:
        thrown(InvalidNodeMeasurementException)

        where:
        temperature | humidity | lightIntensity | carbonDioxide
        -30         | 0        | 0              | 0
        -21         | 0        | 0              | 0
        61          | 0        | 0              | 0
        100         | 0        | 0              | 0
        0           | -10      | 0              | 0
        0           | -1       | 0              | 0
        0           | 101      | 0              | 0
        0           | 110      | 0              | 0
        0           | 0        | -10            | 0
        0           | 0        | -1             | 0
        0           | 0        | 100001         | 0
        0           | 0        | 100111         | 0
        0           | 0        | 0              | -10
        0           | 0        | 0              | -1
        0           | 0        | 0              | 101
        0           | 0        | 0              | 110
    }

    def 'should not throw exception for valid measurement data'() {
        when:
        new NodeMeasurementDocument('id', DateTime.now(), 'node-id', temperature, humidity, lightIntensity, false, carbonDioxide)

        then:
        noExceptionThrown()

        where:
        temperature | humidity | lightIntensity | carbonDioxide
        -20         | 0        | 0              | 0
        60          | 0        | 0              | 0
        0           | 0        | 0              | 0
        0           | 100      | 0              | 0
        0           | 0        | 0              | 0
        0           | 0        | 100000         | 0
        0           | 0        | 0              | 0
        0           | 0        | 0              | 100
    }

}
