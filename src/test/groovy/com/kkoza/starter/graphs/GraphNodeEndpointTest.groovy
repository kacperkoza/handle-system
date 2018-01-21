package com.kkoza.starter.graphs

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.NodeBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared

class GraphNodeEndpointTest extends BaseIntegrationTest {

    @Shared
    def dateTimeNow = DateTime.now()

    @Shared
    String dateQueryParamPattern = 'yyyy-MM-dd HH:mm'

    //@formatter:off                                                                   temp hum light carbon
    def measurement = getNodeMeasurement('1', DateTime.now(), 'node',                   10d, 7d, 0d, 10d)
    def measurement2 = getNodeMeasurement('2', DateTime.now().minusMinutes(5), 'node',  11d, 6d, 0d, 20d)
    def measurement3 = getNodeMeasurement('3', DateTime.now().minusMinutes(10), 'node', 12d, 5d, 3d, 30d)
    def measurement4 = getNodeMeasurement('4', DateTime.now().minusMinutes(15), 'node', 13d, 4d, 1d, 40d)
    def measurement5 = getNodeMeasurement('5', DateTime.now().minusMinutes(20), 'node', 14d, 3d, 5d, 35d)
    //@formatter:on

    def setup() {
        save(new SessionDocument('session-id', 'user-id', DateTime.now().plusDays(1)))
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
        def response = executeGet("fieldName=temperature&deviceId=node&startDate=$startDate&endDate=$endDate")

        then:
        response.body.data.collect({ it.value }) == expectedValues

        where:
        start                        | end                         || expectedValues
        dateTimeNow.minusMinutes(25) | dateTimeNow.plusMinutes(25) || [14d, 13d, 12d, 11d, 10d]
        dateTimeNow.minusMinutes(17) | dateTimeNow.minusMinutes(7) || [13d, 12d]
        dateTimeNow.plusMinutes(10)  | dateTimeNow.plusMinutes(25) || []
    }

    def '[GET] should get data based on queried field'() {
        when:
        def response = executeGet("fieldName=$fieldName&deviceId=node")

        then:
        response.body.data.collect({ it.value })

        where:
        fieldName         || expectedValues
        "temperature"     || [14, 13, 12, 11, 10]
        "humidity"        || [7, 6, 5, 4, 3, 2]
        "carbon"          || [10, 20, 30, 35, 40]
        "light_intensity" || [0, 0, 1, 3, 5]
    }


    def '[GET] should return BAD_REQUEST for unknown field filter'() {
        given:
        def field = unknownFieldName

        when:
        executeGet("fieldName=$field&deviceId=any")

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
        executeGet("fieldName=temperature&deviceId=any&startDate=$startDate&endDate=$endDate")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    NodeMeasurementDocument getNodeMeasurement(String id, DateTime date, String deviceId, Double temperature, Double humidity, Double lightIntensity, Double carbon) {
        return NodeBuilder.create().setId(id).setDate(date).setNodeId(deviceId).setTemperature(temperature)
                .setHumidity(humidity).setLightIntensity(lightIntensity).setCarbonDioxity(carbon).build()
    }

    private def executeGet(String queryParams) {
        def headers = new HttpHeaders()
        headers.add("Cookie", "SESSIONID=session-id")
        def entity = new HttpEntity(headers)
        return restTemplate.exchange(localUrl("users/graphs/nodes?$queryParams"),
                HttpMethod.GET,
                entity,
                ItemsDto)
    }
}
