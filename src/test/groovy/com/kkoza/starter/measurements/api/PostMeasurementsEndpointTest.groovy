package com.kkoza.starter.measurements.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.testutil.HandleBuilder
import com.kkoza.starter.testutil.UserBuilder
import org.joda.time.DateTime

class PostMeasurementsEndpointTest extends BaseIntegrationTest {

    def '[POST] should create new measurement with CREATED [201] status'() {
        given:
        save(HandleBuilder.create().setHandleId('handle-id').setUserId('user-id').buildDocument())
        save(UserBuilder.create('user-id').buildDocument())
        def measurement = new MeasurementDto('handle-id', 0, 0, false, true, false, 10.0, 15)

        when:
        def location = restTemplate.postForLocation(
                localUrl("/measurements"),
                measurement
        ).toASCIIString()

        then:
        location.contains("/measurements/")
        location.substring(location.lastIndexOf("/")).size() > 0
    }
}
