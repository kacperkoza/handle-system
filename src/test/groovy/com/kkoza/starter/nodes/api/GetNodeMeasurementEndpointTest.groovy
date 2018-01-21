package com.kkoza.starter.nodes.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.devices.DeviceType
import com.kkoza.starter.nodes.dto.NodeMeasurementList
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.NodeBuilder
import com.kkoza.starter.testutil.UserBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared
import spock.lang.Unroll

class GetNodeMeasurementEndpointTest extends BaseIntegrationTest {

    @Shared
    def first = NodeBuilder.create()
            .setId('1')
            .setNodeId('node1')
            .setDate(DateTime.now())
            .setMotion(true)
            .setTemperature(1)
            .setLightIntensity(3)
            .setHumidity(10)
            .setCarbonDioxity(10)
            .build()

    @Shared
    def second = NodeBuilder.create()
            .setId('2')
            .setNodeId('node1')
            .setDate(DateTime.now().minusDays(1))
            .setMotion(false)
            .setTemperature(2)
            .setLightIntensity(4)
            .setHumidity(11)
            .setCarbonDioxity(12)
            .build()

    @Shared
    def third = NodeBuilder.create()
            .setId('3')
            .setNodeId('node1')
            .setDate(DateTime.now().minusHours(3))
            .setMotion(false)
            .setTemperature(3)
            .setLightIntensity(2)
            .setHumidity(12)
            .setCarbonDioxity(11)
            .build()

    @Shared
    def user = UserBuilder.create('user-id').setHandles(['node1']).buildDocument()

    @Shared
    def node = DeviceBuilder.create().setId('node1').setDeviceName('node-name').setUserId('user-id').setDeviceType(DeviceType.NODE).buildDocument()

    @Shared
    def node2 = DeviceBuilder.create().setId('node2').setDeviceName('node-name2').setUserId('user-id').setDeviceType(DeviceType.NODE).buildDocument()

    @Shared
    def session = new SessionDocument('session-id', 'user-id', DateTime.now())

    def setup() {
        save(second)
        save(first)
        save(third)
        save(user)
        save(node)
        save(node2)
        save(session)
    }

    @Unroll
    def "[GET] should sort all node measurements by #sortType"() {
        when:
        def response = executeGet("/users/measurements/nodes?sort=$sortType")

        then:
        response.body.measurements.collect({ it.id }) == expectedOrder


        where:
        sortType      || expectedOrder
        'date_latest' || ['1', '3', '2']
        'date_oldest' || ['2', '3', '1']
        'temp_asc'    || ['1', '2', '3']
        'temp_desc'   || ['3', '2', '1']
        'hum_asc'     || ['1', '2', '3']
        'hum_desc'    || ['3', '2', '1']
        'light_asc'   || ['3', '1', '2']
        'light_desc'  || ['2', '1', '3']
        'carbon_asc'  || ['1', '3', '2']
        'carbon_desc' || ['2', '3', '1']
    }

    def "[GET] should return BAD REQUEST [400] for invalid sortType"() {
        when:
        executeGet("/users/measurements/nodes?sort=$invalidSortType")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.getStatusCode().value() == 400

        where:
        invalidSortType << ['ebe ebe', 'data', 'date', 'unknown']
    }

    def "[GET] should sort by 'date_latest' when sort type is not specified"() {
        when:
        def response = executeGet("/users/measurements/nodes")

        then:
        response.body.measurements.id == ['1', '3', '2']
    }

    @Unroll
    def "[GET] should use offset #inputOffset and limit #inputLimit properly"() {
        when:
        def response = executeGet("/users/measurements/nodes?limit=$inputLimit&offset=$inputOffset")

        then:
        with(response.body) {
            limit == inputLimit
            offset == inputOffset
            measurements.id == expectedIds
            count == size
        }

        where: //ids sorted by latest date [1 3 2]
        inputOffset | inputLimit || expectedIds     | size
        1           | 1          || ['3']           | 3
        2           | 2          || ['2']           | 3
        0           | 100        || ['1', '3', '2'] | 3
    }

    def "[GET] should return BAD_REQUEST [400] when limit or offset is lower than 0"() {
        when:
        executeGet("users/measurements/nodes?limit=$limit&offset=$offset")

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
    def "[GET] should return measurements filtered by node-id"() {
        given:
        save(NodeBuilder.create().setNodeId('node2').build())

        when:
        def response = executeGet("users/measurements/nodes?nodes=$handles")

        then:
        with(response.body) {
            measurements.every({ it.nodeName in expectedHandleId })

        }

        where:
        handles       || expectedHandleId
        'node1'       || ['node-name']
        'node2'       || ['node-name2']
        'node1,node2' || ['node-name', 'node-name2']
    }

    def "[GET] should return all measurements when handle filter is not specified"() {
        when:
        def response = executeGet("users/measurements/nodes")

        then:
        with(response.body) {
            measurements.every({ it.nodeName in ['node-name2', 'node-name'] })
        }
    }

    @Unroll
    def "[GET] should return measurements filter by true field"() {
        when:
        def response = executeGet("users/measurements/nodes?filters=$alarmFilters")

        then:
        with(response.body) {
            measurements.every({ it.id in ids })
            measurements.size() == size
        }

        where:
        alarmFilters || ids   | size
        'motion'     || ['1'] | 1
    }

    def '[GET] should return UNPROCESSABLE_ENTITY when start date is after end date'() {
        given:
        def startDate = '2017-10-10 10:50'
        def endDate = '2017-10-10 10:40'

        when:
        executeGet("/users/measurements/nodes?startDate=$startDate&endDate=$endDate")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "[GET] should return measurements filter by start and end date"() {
        when:
        def startDate = formatDate(DateTime.now().minusHours(5))
        def endDate = formatDate(DateTime.now().minusHours(1))
        def response = executeGet("users/measurements/nodes?startDate=$startDate&endDate=$endDate")

        then:
        with(response.body) {
            measurements.collect { it.id } == ['3']
        }
    }

    String formatDate(DateTime dateTime) {
        return dateTime.toString("yyyy-MM-dd HH:mm")
    }

    def executeGet(String endpoint) {
        def headers = new HttpHeaders()
        headers.add("Cookie", "SESSIONID=session-id")
        def entity = new HttpEntity(headers)
        restTemplate.exchange(
                localUrl(endpoint),
                HttpMethod.GET,
                entity,
                NodeMeasurementList.class
        )
    }

}
