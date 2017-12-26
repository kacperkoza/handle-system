package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceType
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.MeasurementBuilder
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
            handleMeasurements.size() == 2
            handleMeasurements.id == ['1', '3']
            handleMeasurements.handleName == ['klamka1', 'klamka2']
        }
    }


    def getHeaders() {
        def headers = new HttpHeaders()
        headers.put("Cookie", ["SESSIONID=session-id"])
        return headers
    }
}
