package com.kkoza.starter

import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.settings.SettingsDocument
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
        mongoTemplate.dropCollection(HandleMeasurementDocument.class)
        mongoTemplate.dropCollection(UserDocument.class)
        mongoTemplate.dropCollection(DeviceDocument.class)
    }

    String localUrl(String endpoint) {
        return "http://localhost:${port}/${endpoint}"
    }

    def save(HandleMeasurementDocument measurement) {
        mongoTemplate.save(measurement)
    }

    def save(UserDocument userDocument) {
        mongoTemplate.save(userDocument)
    }

    def save(SessionDocument session) {
        mongoTemplate.save(session)
    }

    def save(DeviceDocument document) {
        mongoTemplate.save(document)
    }

    def save(SettingsDocument settingsDocument) {
        mongoTemplate.save(settingsDocument)
    }

}
