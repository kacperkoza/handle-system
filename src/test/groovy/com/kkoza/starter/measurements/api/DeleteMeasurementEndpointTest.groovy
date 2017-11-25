package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.measurements.Measurement
import com.kkoza.starter.util.MeasurementBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class DeleteMeasurementEndpointTest extends BaseIntegrationTest {

    def '[DELETE] should return OK [200] when delete measurement by id'() {
        given:
        def id = '123'
        save(MeasurementBuilder.create().setId(id).build())

        when:
        def response = restTemplate.exchange(
                localUrl("/measurements/$id"),
                HttpMethod.DELETE,
                new HttpEntity<Object>(),
                Void)

        then:
        response.statusCode == HttpStatus.OK
        mongoTemplate.findAll(Measurement).size() == 0

    }
}
