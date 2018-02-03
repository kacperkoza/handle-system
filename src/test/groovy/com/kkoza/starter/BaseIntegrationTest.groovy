package com.kkoza.starter

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.settings.SettingsDocument
import com.kkoza.starter.user.UserDocument
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["integration"])
class BaseIntegrationTest extends Specification {

    RestTemplate restTemplate

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    ObjectMapper objectMapper

    @LocalServerPort
    int port

    @Shared
    @ClassRule
    public WireMockRule wireMockRule = new WireMockRule(8089)

    def setup() {
        restTemplate = new RestTemplate()
        mongoTemplate.dropCollection(HandleMeasurementDocument.class)
        mongoTemplate.dropCollection(UserDocument.class)
        mongoTemplate.dropCollection(SessionDocument.class)
        mongoTemplate.dropCollection(DeviceDocument.class)
        mongoTemplate.dropCollection(SettingsDocument.class)
        mongoTemplate.dropCollection(NodeMeasurementDocument.class)
    }

    def stubSmsClient() {
        wireMockRule
                .stubFor(post(urlEqualTo("/text"))
                .willReturn(aResponse()
                .withStatus(200)))
    }

    def stubSmsClient(int statusCode, def requestBody) {
        wireMockRule
                .stubFor(post(urlEqualTo("/text"))
                .willReturn(aResponse()
                .withStatus(statusCode))
                .withRequestBody(containing(objectMapper.writeValueAsString(requestBody))))
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

    def save(NodeMeasurementDocument nodeMeasurementDocument) {
        mongoTemplate.save(nodeMeasurementDocument)
    }

}
