package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.measurements.HandleMeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.MeasurementBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class DeleteMeasurementEndpointTest extends BaseIntegrationTest {

    def setup() {
        save(new SessionDocument(
                'sess',
                'user-id',
                DateTime.now()))
    }

    def '[DELETE] should return OK [200] when delete measurement by id'() {
        given:
        def id = '123'
        save(MeasurementBuilder.create().setId(id).build())

        when:
        def response = executePost(id)

        then:
        response.statusCode == HttpStatus.OK
        mongoTemplate.findAll(HandleMeasurementDocument).size() == 0
    }

    private ResponseEntity<Void> executePost(String id) {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=sess")
        restTemplate.exchange(
                localUrl("users/measurements/handles/$id"),
                HttpMethod.DELETE,
                new HttpEntity<Object>(headers),
                Void)
    }
}
