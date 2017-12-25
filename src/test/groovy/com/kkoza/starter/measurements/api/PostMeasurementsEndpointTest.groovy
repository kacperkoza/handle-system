package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.testutil.DeviceBuilder
import com.kkoza.starter.testutil.UserBuilder

class PostMeasurementsEndpointTest extends BaseIntegrationTest {

    def '[POST] should create new measurement with CREATED [201] status'() {
        given:
        save(DeviceBuilder.create().setId('device-id').setUserId('user-id').buildDocument())
        save(UserBuilder.create('user-id').buildDocument())
        def measurement = new MeasurementDto('device-id', 0, 0, false, true, false, 10.0, 15)

        when:
        def location = restTemplate.postForLocation(
                localUrl("/measurements/handles"),
                measurement
        ).toASCIIString()

        then:
        location.contains("/measurements/handles")
        location.substring(location.lastIndexOf("/")).size() > 0
    }
}
