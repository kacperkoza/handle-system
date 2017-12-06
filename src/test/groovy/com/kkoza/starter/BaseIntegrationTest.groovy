package com.kkoza.starter

import com.kkoza.starter.handles.HandleDocument
import com.kkoza.starter.measurements.MeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.user.UserDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["integration"])
class BaseIntegrationTest extends Specification {

    RestTemplate restTemplate

    @Autowired
    MongoTemplate mongoTemplate

    @LocalServerPort
    int port

    def setup() {
        restTemplate = new RestTemplate()
        mongoTemplate.dropCollection(MeasurementDocument.class)
        mongoTemplate.dropCollection(UserDocument.class)
        mongoTemplate.dropCollection(HandleDocument.class)
    }

    String localUrl(String endpoint) {
        return "http://localhost:${port}/${endpoint}"
    }

    def save(MeasurementDocument measurement) {
        mongoTemplate.save(measurement)
    }

    def save(UserDocument userDocument) {
        mongoTemplate.save(userDocument)
    }

    def save(SessionDocument session) {
        mongoTemplate.save(session)
    }

    def save(HandleDocument document) {
        mongoTemplate.save(document)
    }
}
