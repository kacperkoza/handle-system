package com.kkoza.starter.handles.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceType
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.MeasurementBuilder
import com.kkoza.starter.testutil.NodeBuilder
import org.joda.time.DateTime
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class LatestMeasurementEndpointTest extends BaseIntegrationTest {

    def setup() {
        save(new SessionDocument('session-id', 'user-id', DateTime.now().plusMinutes(5)))
        save(MeasurementBuilder.create().setId('1').setHandleId('handle1').setDate(DateTime.now().plusMinutes(1)).build())
        save(MeasurementBuilder.create().setId('2').setHandleId('handle1').setDate(DateTime.now()).build())
        save(MeasurementBuilder.create().setId('3').setHandleId('handle2').setDate(DateTime.now()).build())
        save(MeasurementBuilder.create().setId('4').setHandleId('handle2').setDate(DateTime.now().minusSeconds(5)).build())
        save(new DeviceDocument('handle1', 'klamka1', 'user-id', DeviceType.HANDLE))
        save(new DeviceDocument('handle2', 'klamka2', 'user-id', DeviceType.HANDLE))

        save(NodeBuilder.create().setId('5').setNodeId('node1').setDate(DateTime.now()).build())
        save(NodeBuilder.create().setId('6').setNodeId('node1').setDate(DateTime.now().minusMinutes(5)).build())
        save(NodeBuilder.create().setId('7').setNodeId('node2').setDate(DateTime.now().plusMinutes(5)).build())
        save(NodeBuilder.create().setId('8').setNodeId('node2').setDate(DateTime.now()).build())
        save(new DeviceDocument('node1', 'urzadzenie1', 'user-id', DeviceType.NODE))
        save(new DeviceDocument('node2', 'urzadzenie2', 'user-id', DeviceType.NODE))
    }

    def 'should return the most recent handles measurements'() {
        when:
        def response = restTemplate.exchange(
                localUrl('users/measurements/latest'),
                HttpMethod.GET,
                new HttpEntity<Object>(getHeaders()),
                LatestMeasurementResponse.class)

        then:
        with(response.body) {
            handleMeasurements.id == ['1', '3']
            handleMeasurements.handleName == ['klamka1', 'klamka2']

            nodes.id == ['5', '7']
            nodes.nodeName == ['urzadzenie1', 'urzadzenie2']
        }
    }

    def 'should return empty list when no measurements found'() {
        given:
        mongoTemplate.dropCollection(NodeMeasurementDocument.class)
        mongoTemplate.dropCollection(HandleMeasurementDocument.class)

        when:
        def response = restTemplate.exchange(
                localUrl('users/measurements/latest'),
                HttpMethod.GET,
                new HttpEntity<Object>(getHeaders()),
                LatestMeasurementResponse.class)

        then:
        with(response.body) {
            handleMeasurements.size() == 0
            nodes.size() == 0
        }
    }

    def getHeaders() {
        def headers = new HttpHeaders()
        headers.put("Cookie", ["SESSIONID=session-id"])
        return headers
    }
}
