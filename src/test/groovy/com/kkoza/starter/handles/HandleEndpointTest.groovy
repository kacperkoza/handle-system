package com.kkoza.starter.handles

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.session.SessionDocument
import com.kkoza.starter.testutil.HandleBuilder
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

class HandleEndpointTest extends BaseIntegrationTest {

    def sessionId = 'sess-id'

    def setup() {
        save(new SessionDocument(sessionId, 'user-id', DateTime.now()))
    }


    def '[POST] should return CREATED (201) after successful adding new handle'() {
        given:
        def dto = HandleBuilder.create().setHandleId('handle-id').setHandleName('handle-name').buildDto()

        when:
        def location = restTemplate.postForLocation(localUrl('/users/handles'), dto, getHeadersWithSessionCookie()).toASCIIString()
        def handle = mongoTemplate.findOne(new Query(), HandleDocument)

        then:
        location == "/users/handles/${handle.id}"
        handle.userId == dto.id
        handle.name == dto.name
    }

    def '[POST] should return UNPROCESSABLE_ENTITY (422) when handle-id already exists'() {
        given:
        saveHandle('handle-id', 'handle-name')
        def dto = HandleBuilder.create().setHandleId('handle-id').setHandleName('another').buildDto()

        when:
        restTemplate.postForLocation(localUrl('/users/handles'), dto, getHeadersWithSessionCookie())

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[POST] should return UNPROCESSABLE_ENTTITY (422) when name is empty'() {
        given:
        def dto = HandleBuilder.create().setHandleId('another').setHandleName('').buildDto()

        when:
        restTemplate.postForLocation(localUrl('/users/handles'), dto,)

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
        def response = restTemplate.exchange(
                localUrl("/users/handles"),
                HttpMethod.GET,
                new HttpEntity<Object>(getHeadersWithSessionCookie()),
                HandleList.class)

        then:
        response.body.handles == [handle1.buildDto(), handle2.buildDto()]
    }

    def '[GET] should return user handle by id with OK [200]'() {
        given:
        save(HandleBuilder.create().setHandleId('id').setHandleName('name').setUserId('user-id').buildDocument())

        when:
        def response = restTemplate.exchange(localUrl("/users/handles/id"),
                HttpMethod.GET,
                new HttpEntity<Object>(getHeadersWithSessionCookie()),
                HandleDto)

        then:
        with(response.body) {
            id == 'id'
            name == 'name'
        }
    }

    def '[GET] should return NOT_FOUND [404] when handle-id was not found'() {
        when:
        restTemplate.getForEntity(localUrl("/users/handles/id"), HandleDto)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def '[PUT] should override existing handle name with OK (200)'() {
        given:
        save(new HandleDocument('handle-id', 'handle-name', 'user-id'))

        when:
        restTemplate.put(localUrl("users/handles/handle-id"), '{"name":"new-name"}')
        HandleDocument handleDocument = mongoTemplate.findOne(new Query(), HandleDocument.class)

        then:
        with(handleDocument) {
            id == 'handle-id'
            userId == 'user-id'
            name == 'new-name'
        }
    }

    def '[PUT] should return UNPROCESSABLE_ENTITY [422] for empty name'() {
        when:
        restTemplate.put(localUrl("users/handles/handle-id"), '{"name":""}')

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[DELETE] should delete existing handle by id with status OK (200)'() {
        given:
        save(HandleBuilder.create().setHandleId('handle-id').buildDocument())

        when:
        restTemplate.exchange(localUrl("/users/handles/handle-id"),
                HttpMethod.DELETE,
                new HttpEntity<Object>(getHeadersWithSessionCookie()),
                Void)

        then:
        mongoTemplate.count(new Query(), HandleDocument.class) == 0
    }

    private def saveHandle(String handleId, String handleName) {
        save(HandleBuilder.create().setHandleId(handleId).setHandleName(handleName).buildDocument())
    }

    def getHeadersWithSessionCookie() {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=$sessionId")
        return headers
    }


}
