package com.kkoza.starter

import com.github.fakemongo.Fongo
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

    MongoTemplate mongoTemplate

    @LocalServerPort
    int port

    def setup() {
        mongoTemplate = new MongoTemplate(new Fongo("test_db").getMongo(), "test")
        restTemplate = new RestTemplate()
    }

    String localUrl(String endpoint) {
        return "http://localhost:${port}/${endpoint}"
    }
}
