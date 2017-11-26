package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.testutil.MeasurementBuilder
import org.joda.time.DateTime
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared
import spock.lang.Unroll

class GetMeasurementsEndpointTest extends BaseIntegrationTest {

    @Shared
    def first = MeasurementBuilder.create()
            .setId('1')
            .setDate(DateTime.now().minusHours(1))
            .build()

    @Shared
    def second = MeasurementBuilder.create()
            .setId('2')
            .setDate(DateTime.now().minusDays(1))
            .build()

    @Shared
    def third = MeasurementBuilder.create()
            .setId('3')
            .setDate(DateTime.now().minusDays(6))
            .build()

    def setup() {
        save(second)
        save(first)
        save(third)

    }

    @Unroll
    def "[GET] should sort all measurements by #sortType"() {
        when:
        def response = executeGet("/measurements?sort=$sortType")

        then:
        response.body.measurements.collect({ it.id }) == expectedOrder


        where:
        sortType      || expectedOrder
        'date_latest' || ['1', '2', '3']
        'date_oldest' || ['3', '2', '1']
    }

    def "[GET] should return BAD REQUEST [400] for invalid sortType"() {
        when:
        executeGet("/measurements?sort=$invalidSortType")

        then:
        def ex = thrown(HttpClientErrorException)
        ex.getStatusCode().value() == 400

        where:
        invalidSortType << ['ebe ebe', 'data', 'date', 'unknown']
    }

    def "[GET] should sort by 'date_latest' when sort type is not specified"() {
        when:
        def response = executeGet("/measurements")

        then:
        response.body.measurements.collect({ it.id }) == ['1', '2', '3']
    }

    @Unroll
    def "[GET] should use offset #inputOffset and limit #inputLimit properly"() {
        given:
        save(MeasurementBuilder.create().setId('4').setDate(DateTime.now()).build())
        save(MeasurementBuilder.create().setId('5').setDate(DateTime.now().minusMillis(100)).build())
        save(MeasurementBuilder.create().setId('6').setDate(DateTime.now().minusMillis(200)).build())

        when:
        def response = executeGet("/measurements?limit=$inputLimit&offset=$inputOffset")

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

    def "[GET] should return BAD_REQUEST [400] when limit or offset is lower than 0"() {
        when:
        executeGet("/measurements?limit=$limit&offset=$offset")

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

    def executeGet(String endpoint) {
        restTemplate.getForEntity(
                localUrl(endpoint),
                MeasurementList.class)
    }

}
