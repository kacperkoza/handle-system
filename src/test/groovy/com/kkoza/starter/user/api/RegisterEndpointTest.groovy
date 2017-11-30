package com.kkoza.starter.user.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.dto.UserDto
import com.kkoza.starter.testutil.UserBuilder
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared

class RegisterEndpointTest extends BaseIntegrationTest {

    def '[POST] should add new user with valid data with CREATED [201] status'() {
        given:
        UserDto validUserDto = UserBuilder.create(null)
                .setLogin('valid-existingLogin')
                .setPassword('valid-existingPassword')
                .setName('valid-name')
                .setSurname('valid-surname')
                .setEmail('email@gmail.com')
                .setPhoneNumber('123456789')
                .buildDto()

        when:
        def location = postForLocation(validUserDto).toASCIIString()

        then:
        location.contains('http://localhost:8080/users/')
        location.substring(location.lastIndexOf('/')).size() > 0
    }

    def '[POST] should return UNPROCESSABLE_ENTITY [422] when new user\'s login already exists'() {
        given:
        def userBuilder = UserBuilder.create(null).setLogin('at-least-6-characters')
        save(userBuilder.buildDocument())

        when:
        postForLocation(userBuilder.buildDto())

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[POST] should return UNPROCESSABLE_ENTITY [422] when new user\'s email already exists'() {
        given:
        def userBuilder = UserBuilder.create(null).setEmail('email@email.com')
        save(userBuilder.buildDocument())

        when:
        postForLocation(userBuilder.buildDto())

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[POST] should return UNPROCESSABLE_ENTITY [422] for invalid input user dto'() {
        given:
        def userDto = UserBuilder.create(null).setEmail('invalid').buildDto()

        when:
        postForLocation(userDto)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def '[PUT] should update existing use with OK [200] status'() {
        given:
        def user = UserBuilder.create(null).setLogin('example-existingLogin').setPassword('example-existingPassword').buildDocument()
        save(user)
        def userDto = UserBuilder.create(user.userId).setLogin('new-existingLogin').setPassword('new-existingPassword').buildDto()

        when:
        def response = restTemplate.exchange(
                localUrl("/users"),
                HttpMethod.PUT,
                new HttpEntity<Object>(userDto),
                Void
        )

        then:
        response.statusCode == HttpStatus.OK
        UserDocument userDocument = mongoTemplate.findOne(new Query(), UserDocument.class)
        userDocument.login == 'new-existingLogin'
        userDocument.password == 'new-existingPassword'
    }

    private URI postForLocation(UserDto userDto) {
        restTemplate.postForLocation(
                localUrl('/register'),
                userDto
        )
    }
}
