package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.MeasurementBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared

class GraphEndpointTest extends BaseIntegrationTest {

    @Shared
    def dateTimeNow = DateTime.now()

    @Shared
    String dateQueryParamPattern = 'yyyy-MM-dd HH:mm'

    def measurement = MeasurementBuilder.create().setId('1').setDate(dateTimeNow).setHandleId('handleAlarmFilterEx-1').setTemperature(1.0).setSound(10.0).build()
    def measurement2 = MeasurementBuilder.create().setId('2').setDate(dateTimeNow.minusMinutes(5)).setHandleId('handleAlarmFilterEx-1').setTemperature(2.0).setSound(11.0).build()
    def measurement3 = MeasurementBuilder.create().setId('3').setDate(dateTimeNow.minusMinutes(10)).setHandleId('handleAlarmFilterEx-1').setTemperature(3.0).setSound(12.0).build()
    def measurement4 = MeasurementBuilder.create().setId('4').setDate(dateTimeNow.minusMinutes(15)).setHandleId('handleAlarmFilterEx-1').setTemperature(4.0).setSound(13.0).build()
    def measurement5 = MeasurementBuilder.create().setId('5').setDate(dateTimeNow.minusMinutes(20)).setHandleId('handleAlarmFilterEx-1').setTemperature(5.0).setSound(14.0).build()

    def setup() {
        save(new SessionDocument('session-id', 'any', DateTime.now().plusDays(1)))
        save(measurement5)
        save(measurement4)
        save(measurement3)
        save(measurement2)
        save(measurement)
    }

    def '[GET] should filter measurements by start and date end'() {
        given:
        def startDate = start.toString(dateQueryParamPattern)
        def endDate = end.toString(dateQueryParamPattern)

        when:
        def response = executeGet("fieldName=temperature&handleId=handleAlarmFilterEx-1&startDate=$startDate&endDate=$endDate")

        then:
        response.body.data.collect({ it.value }) == expectedValues

        where:
        start                          | end                           || expectedValues
        dateTimeNow.minusMinutes(1000) | dateTimeNow.plusMinutes(1000) || [5.0d, 4.0d, 3.0d, 2.0d, 1.0d]
        dateTimeNow.minusMinutes(17)   | dateTimeNow.minusMinutes(7)   || [4.0d, 3.0d]
        dateTimeNow.minusMinutes(1000) | dateTimeNow.minusMinutes(25)  || []
    }

    def '[GET] should get data based on queried field'() {
        when:
        def response = executeGet("fieldName=$fieldName&handleId=handleAlarmFilterEx-1")

        then:
        response.body.data.collect({ it.value })

        where:
        fieldName     || expectedValues
        "temperature" || [1.0, 2.0, 3.0, 4.0, 5.0]
        "sound_level" || [10.0, 11.0, 12.0, 13.0, 14.0, 15.0]
    }


    def '[GET] should return BAD_REQUEST for unknown field filter'() {
        given:
        def field = unknownFieldName

        when:
        executeGet("fieldName=$field&handleId=any")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST

        where:
        unknownFieldName << ['unknown', 'klamka', 'temperatur', 'soundlevel', 'sound']
    }

    def '[GET] should return UNPROCESSABLE_ENTITY when start date is after end date'() {
        given:
        def startDate = '2017-10-10 10:50'
        def endDate = '2017-10-10 10:40'

        when:
        executeGet("fieldName=temperature&handleId=any&startDate=$startDate&endDate=$endDate")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY

        where:
        unknownFieldName << ['unknown', 'klamka', 'temperatur', 'soundlevel', 'sound']
    }


    private def executeGet(String queryParams) {
        def headers = new HttpHeaders()
        headers.add("Cookie", "SESSIONID=session-id")
        def entity = new HttpEntity(headers)
        return restTemplate.exchange(localUrl("users/graphs/measurements?$queryParams"),
                HttpMethod.GET,
                entity,
                ItemsDto)
    }
}
