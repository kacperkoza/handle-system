package com.kkoza.starter.handles.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.handles.dto.MeasurementList
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.HandleBuilder
import com.kkoza.starter.testutil.UserBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared
import spock.lang.Unroll

class GetMeasurementsEndpointTest extends BaseIntegrationTest {

    @Shared
    def first = HandleBuilder.create()
            .setId('1')
            .setHandleId('handle1')
            .setAlarm(true, true, false)
            .setTemperature(10.0d)
            .setSound(10.0d)
            .setDate(DateTime.now().minusHours(1))
            .build()

    @Shared
    def second = HandleBuilder.create()
            .setId('2')
            .setHandleId('handle1')
            .setAlarm(true, false, true)
            .setTemperature(15.0d)
            .setSound(15.0d)
            .setDate(DateTime.now().minusDays(1))
            .build()

    @Shared
    def third = HandleBuilder.create()
            .setId('3')
            .setHandleId('handle1')
            .setAlarm(false, false, true)
            .setTemperature(5.0d)
            .setSound(5.0d)
            .setDate(DateTime.now().minusDays(6))
            .build()

    @Shared
    def user = UserBuilder.create('user-id').setHandles(['handle1']).buildDocument()

    @Shared
    def handle = DeviceBuilder.create().setId('handle1').setDeviceName('node-name').setUserId('user-id').buildDocument()

    @Shared
    def handle2 = DeviceBuilder.create().setId('node2').setDeviceName('node-name2').setUserId('user-id').buildDocument()

    @Shared
    def session = new SessionDocument('session-id', 'user-id', DateTime.now())

    def setup() {
        save(second)
        save(first)
        save(third)
        save(user)
        save(handle)
        save(handle2)
        save(session)
    }

    @Unroll
    def "[GET] should sort all measurements by #sortType"() {
        when:
        def response = executeGet("/users/measurements/handles?sort=$sortType")

        then:
        response.body.measurements.collect({ it.id }) == expectedOrder


        where:
        sortType      || expectedOrder
        'date_latest' || ['1', '2', '3']
        'date_oldest' || ['3', '2', '1']
    }

    def "[GET] should return BAD REQUEST [400] for invalid sortType"() {
        when:
        executeGet("/users/measurements/handles?sort=$invalidSortType")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.getStatusCode().value() == 400

        where:
        invalidSortType << ['ebe ebe', 'data', 'date', 'unknown']
    }

    def "[GET] should sort by 'date_latest' when sort type is not specified"() {
        when:
        def response = executeGet("/users/measurements/handles")

        then:
        response.body.measurements.collect({ it.id }) == ['1', '2', '3']
    }

    @Unroll
    def "[GET] should use offset #inputOffset and limit #inputLimit properly"() {
        given:
        save(HandleBuilder.create().setHandleId('handle1').setId('4').setDate(DateTime.now()).build())
        save(HandleBuilder.create().setHandleId('handle1').setId('5').setDate(DateTime.now().minusMillis(100)).build())
        save(HandleBuilder.create().setHandleId('handle1').setId('6').setDate(DateTime.now().minusMillis(200)).build())

        when:
        def response = executeGet("/users/measurements/handles?limit=$inputLimit&offset=$inputOffset")

        then:
        with(response.body) {
            limit == inputLimit
            offset == inputOffset
            measurements.id == expectedIds
            count == size
        }

        where: //ids sorted by latest date [4 5 6 1 2 3]
        inputOffset | inputLimit || expectedIds                    | size
        1           | 1          || ['5']                          | 6
        2           | 2          || ['6', '1']                     | 6
        0           | 100        || ['4', '5', '6', '1', '2', '3'] | 6
    }

    @Unroll
    def "[GET] should sort by temperature #sort"() {
        when:
        def response = executeGet("/users/measurements/handles?sort=$sort")

        then:
        response.body.measurements.collect({ it.id }) == expectedOrder

        where:
        sort        || expectedOrder
        'temp_asc'  || ['3', '1', '2']
        'temp_desc' || ['2', '1', '3']
    }

    @Unroll
    def "[GET] should sort by sound level #sort"() {
        when:
        def response = executeGet("/users/measurements/handles?sort=$sort")

        then:
        response.body.measurements.collect({ it.id }) == expectedOrder

        where:
        sort         || expectedOrder
        'sound_asc'  || ['3', '1', '2']
        'sound_desc' || ['2', '1', '3']
    }

    def "[GET] should return BAD_REQUEST [400] when limit or offset is lower than 0"() {
        when:
        executeGet("users/measurements/handles?limit=$limit&offset=$offset")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400

        where:
        limit | offset
        -100  | 5
        -1    | 5
        5     | -1
        5     | -100
    }

    @Unroll
    def "[GET] should return measurements filtered by handle-id"() {
        when:
        def response = executeGet("users/measurements/handles?handles=$handles")

        then:
        with(response.body) {
            measurements.every({ it.handleName in expectedHandleId })

        }

        where:
        handles           || expectedHandleId
        'handle1'         || ['node-name']
        'node2'         || ['node-name2']
        'handle1,node2' || ['node-name', 'node-name2']
    }

    def "[GET] should return all measurements when handle filter is not specified"() {
        given:
        def expectedHandleName = ['node-name', 'node-name-2']

        when:
        def response = executeGet("users/measurements/handles")

        then:
        with(response.body) {
            measurements.every({ it.handleName in expectedHandleName })
        }
    }

    @Unroll
    def "[GET] should return measurements filtered by true alarms"() {
        when:
        def response = executeGet("users/measurements/handles?alarms=$alarmFilters")

        then:
        with(response.body) {
            measurements.every({ it.id in measurementsIds })
            measurements.size() == size
        }

        where:
        alarmFilters     || measurementsIds | size
        'fire'           || ['1', '2']      | 2
        'burglary,frost' || []              | 0
        'frost,fire'     || ['2']           | 1
    }

    def executeGet(String endpoint) {
        def headers = new HttpHeaders()
        headers.add("Cookie", "SESSIONID=session-id")
        def entity = new HttpEntity(headers)
        restTemplate.exchange(
                localUrl(endpoint),
                HttpMethod.GET,
                entity,
                MeasurementList.class
        )
    }

}
