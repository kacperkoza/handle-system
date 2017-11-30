package com.kkoza.starter

import com.kkoza.starter.measurements.Measurement
import com.kkoza.starter.session.Session
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
        mongoTemplate.dropCollection(Measurement.class)
        mongoTemplate.dropCollection(UserDocument.class)
    }

    String localUrl(String endpoint) {
        return "http://localhost:${port}/${endpoint}"
    }

    def save(Measurement measurement) {
        mongoTemplate.save(measurement)
    }

    def save(UserDocument userDocument) {
        mongoTemplate.save(userDocument)
    }

    def save(Session session) {
        mongoTemplate.save(session)
    }
}
