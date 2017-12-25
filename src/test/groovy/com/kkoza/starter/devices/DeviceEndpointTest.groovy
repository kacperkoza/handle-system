package com.kkoza.starter.devices

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.devices.api.HandleList
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.DeviceBuilder
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpMethod.PUT

class DeviceEndpointTest extends BaseIntegrationTest {

    String sessionId = 'session-id'

    def setup() {
        save(new SessionDocument(sessionId, 'user-id', DateTime.now()))
    }

    def '[POST] should return CREATED (201) after successful adding new handle'() {
        given:
        DeviceDto dto = DeviceBuilder.create().setId('handle-id').setDeviceName('handle-name').buildDto()

        when:
        String location = execute('/users/devices', POST, dto, Void).headers.getFirst("Location")
        DeviceDocument handle = mongoTemplate.findOne(new Query(), DeviceDocument)

        then:
        location == "/users/devices/handle-id"
        handle.name == dto.name
    }

    def '[POST] should return UNPROCESSABLE_ENTITY (422) when handle-id already exists'() {
        given:
        saveHandle('handle-id', 'handle-name')
        DeviceDto dto = DeviceBuilder.create().setId('handle-id').setDeviceName('another').buildDto()

        when:
        execute('/users/devices', POST, dto, Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[POST] should return UNPROCESSABLE_ENTTITY (422) when name is blank'() {
        given:
        def dto = DeviceBuilder.create().setId('another').setDeviceName('').buildDto()

        when:
        execute('/users/devices', POST, dto, Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[GET] should return user\'s all handles with OK (200)'() {
        given:
        def handle1 = DeviceBuilder.create().setId('id').setDeviceName('name').setUserId('user-id')
        def handle2 = DeviceBuilder.create().setId('id2').setDeviceName('name2').setUserId('user-id')
        save(handle1.buildDocument())
        save(handle2.buildDocument())

        when:
        ResponseEntity<HandleList> response = execute('/users/devices', GET, null, HandleList)

        then:
        response.body.devices == [handle1.buildDto(), handle2.buildDto()]
    }

    def '[GET] should return user handle by id with OK [200]'() {
        given:
        save(DeviceBuilder.create().setId('id').setDeviceName('name').setUserId('user-id').buildDocument())

        when:
        ResponseEntity<DeviceDto> response = execute('/users/devices/id', GET, null, DeviceDto)

        then:
        with(response.body) {
            id == 'id'
            name == 'name'
        }
    }

    def '[GET] should return NOT_FOUND [404] when handle-id was not found'() {
        when:
        execute('/users/devices/id', GET, null, DeviceDto)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def '[PUT] should override existing handle name with OK (200)'() {
        given:
        save(new DeviceDocument('handle-id', 'handle-name', 'user-id', DeviceType.HANDLE))
        def dto = DeviceBuilder.create().setId('handle-id').setDeviceName('new-name').setDeviceType(DeviceType.NODE).buildDto()

        when:
        execute('/users/devices/handle-id', PUT, dto, Void)
        DeviceDocument handleDocument = mongoTemplate.findOne(new Query(), DeviceDocument.class)

        then:
        with(handleDocument) {
            id == 'handle-id'
            userId == 'user-id'
            name == 'new-name'
            deviceType == DeviceType.NODE
        }
    }

    def '[PUT] should return UNPROCESSABLE_ENTITY [422] for blank name'() {
        when:
        execute('/users/devices/handle-id', PUT, DeviceBuilder.create().setDeviceName(' ').buildDto(), Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[DELETE] should delete existing handle by id with status OK (200)'() {
        given:
        save(DeviceBuilder.create().setId('handle-id').buildDocument())

        when:
        execute('/users/devices/handle-id', DELETE, null, Void)

        then:
        mongoTemplate.count(new Query(), DeviceDocument.class) == 0
    }

    private def saveHandle(String handleId, String handleName) {
        save(DeviceBuilder.create().setId(handleId).setDeviceName(handleName).buildDocument())
    }

    private <T> ResponseEntity<T> execute(String endpoint, HttpMethod httpMethod, Object body, Class<T> responseType) {
        return restTemplate.exchange(localUrl(endpoint), httpMethod, generateEntityWithValidSession(body), responseType)
    }

    private def generateEntityWithValidSession(def body) {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=$sessionId")
        return new HttpEntity<>(body, headers)
    }

}
