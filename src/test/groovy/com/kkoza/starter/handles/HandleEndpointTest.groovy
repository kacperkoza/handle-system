package com.kkoza.starter.handles

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.handles.api.HandleDto
import com.kkoza.starter.handles.api.HandleList
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.HandleBuilder
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

class HandleEndpointTest extends BaseIntegrationTest {

    String sessionId = 'session-id'

    def setup() {
        save(new SessionDocument(sessionId, 'user-id', DateTime.now()))
    }

    def '[POST] should return CREATED (201) after successful adding new handle'() {
        given:
        HandleDto dto = HandleBuilder.create().setHandleId('handle-id').setHandleName('handle-name').buildDto()

        when:
        String location = execute('/users/handles', POST, dto, Void).headers.getFirst("Location")
        HandleDocument handle = mongoTemplate.findOne(new Query(), HandleDocument)

        then:
        location == "/users/handles/handle-id"
        handle.name == dto.name
    }

    def '[POST] should return UNPROCESSABLE_ENTITY (422) when handle-id already exists'() {
        given:
        saveHandle('handle-id', 'handle-name')
        HandleDto dto = HandleBuilder.create().setHandleId('handle-id').setHandleName('another').buildDto()

        when:
        execute('/users/handles', POST, dto, Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[POST] should return UNPROCESSABLE_ENTTITY (422) when name is blank'() {
        given:
        def dto = HandleBuilder.create().setHandleId('another').setHandleName('').buildDto()

        when:
        execute('/users/handles', POST, dto, Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[GET] should return user\'s all handles with OK (200)'() {
        given:
        def handle1 = HandleBuilder.create().setHandleId('id').setHandleName('name').setUserId('user-id')
        def handle2 = HandleBuilder.create().setHandleId('id2').setHandleName('name2').setUserId('user-id')
        save(handle1.buildDocument())
        save(handle2.buildDocument())

        when:
        ResponseEntity<HandleList> response = execute('/users/handles', GET, null, HandleList)

        then:
        response.body.handles == [handle1.buildDto(), handle2.buildDto()]
    }

    def '[GET] should return user handle by id with OK [200]'() {
        given:
        save(HandleBuilder.create().setHandleId('id').setHandleName('name').setUserId('user-id').buildDocument())

        when:
        ResponseEntity<HandleDto> response = execute('/users/handles/id', GET, null, HandleDto)

        then:
        with(response.body) {
            id == 'id'
            name == 'name'
        }
    }

    def '[GET] should return NOT_FOUND [404] when handle-id was not found'() {
        when:
        execute('/users/handles/id', GET, null, HandleDto)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def '[PUT] should override existing handle name with OK (200)'() {
        given:
        save(new HandleDocument('handle-id', 'handle-name', 'user-id'))

        when:
        execute('/users/handles/handle-id', PUT, 'new-name', Void)
        HandleDocument handleDocument = mongoTemplate.findOne(new Query(), HandleDocument.class)

        then:
        with(handleDocument) {
            id == 'handle-id'
            userId == 'user-id'
            name == 'new-name'
        }
    }

    def '[PUT] should return UNPROCESSABLE_ENTITY [422] for blank name'() {
        when:
        execute('/users/handles/handle-id', PUT, ' ', Void)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[DELETE] should delete existing handle by id with status OK (200)'() {
        given:
        save(HandleBuilder.create().setHandleId('handle-id').buildDocument())

        when:
        execute('/users/handles/handle-id', DELETE, null, Void)

        then:
        mongoTemplate.count(new Query(), HandleDocument.class) == 0
    }

    private def saveHandle(String handleId, String handleName) {
        save(HandleBuilder.create().setHandleId(handleId).setHandleName(handleName).buildDocument())
    }

    private def generateEntityWithValidSession(def body) {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=$sessionId")
        return new HttpEntity<>(body, headers)
    }

    private <T> ResponseEntity<T> execute(String endpoint, HttpMethod httpMethod, Object body, Class<T> responseType) {
        return restTemplate.exchange(localUrl(endpoint), httpMethod, generateEntityWithValidSession(body), responseType)
    }

}
