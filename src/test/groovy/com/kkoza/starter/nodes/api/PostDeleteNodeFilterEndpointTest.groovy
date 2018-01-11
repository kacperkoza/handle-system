package com.kkoza.starter.nodes.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.nodes.dto.NodeMeasurementDto
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.settings.SettingsDocument
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.NodeBuilder
import com.kkoza.starter.testutil.UserBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class PostDeleteNodeFilterEndpointTest extends BaseIntegrationTest {

    def setup() {
        save(new SessionDocument(
                'sess',
                'user-id',
                DateTime.now()))
    }

    def '[DELETE] should return OK [200] when delete measurement by id'() {
        given:
        def id = '123'
        save(NodeBuilder.create().setId(id).build())

        when:
        def response = restTemplate.exchange(
                localUrl("users/measurements/nodes/$id"),
                HttpMethod.DELETE,
                new HttpEntity<Object>(getHeadersWithSession()),
                Void)

        then:
        response.statusCode == HttpStatus.OK
        mongoTemplate.findAll(HandleMeasurementDocument).size() == 0
    }

    def '[POST] should create new measurement with CREATED [201] status'() {
        given:
        save(DeviceBuilder.create().setId('device-id').setUserId('user-id').buildDocument())
        save(UserBuilder.create('user-id').buildDocument())
        save(new SettingsDocument('user-id', 0, false))
        def measurement = new NodeMeasurementDto('device-id', 0d, 0d, 0d, true,15d)

        when:
        def location = restTemplate.postForLocation(
                localUrl("/measurements/nodes"),
                measurement
        ).toASCIIString()

        then:
        location.contains("/measurements/nodes")
        location.substring(location.lastIndexOf("/")).size() > 0
        mongoTemplate.findAll(NodeMeasurementDocument.class).size() == 1
    }

    private def getHeadersWithSession() {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=sess")
        return headers
    }


}
