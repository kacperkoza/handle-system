package com.kkoza.starter.user.api

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.testutil.UserBuilder
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.dto.LoginDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared

class LoginIntegrationTest extends BaseIntegrationTest {

    @Shared
    String existingEmail = 'existingEmail'

    @Shared
    String existingPassword = 'existingPassword'

    @Shared
    UserDocument userDocument = UserBuilder.create('user-id').setEmail(existingEmail).setPassword(existingPassword).buildDocument()

    def setup() {
        save(userDocument)
    }

    def '[POST] should return OK [200] after succesful login'() {
        given:
        def loginDto = new LoginDto(existingEmail, existingPassword)

        when:
        def response = executePost(loginDto)

        then:
        response.statusCode == HttpStatus.OK
    }

    def '[POST] should return \'Set-Cookie\' header after successful login'() {
        given:
        def loginDto = new LoginDto(existingEmail, existingPassword)

        when:
        def response = executePost(loginDto)
        String cookie = response.getHeaders().get("Set-Cookie")

        then:
        cookie.contains('SESSIONID=')


    }

    def '[POST] should return UNAUTHORIZED [401] when login or password was wrong'() {
        given:
        def invalidLoginDto = new LoginDto('wrong', 'wrong')

        when:
        executePost(invalidLoginDto)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.UNAUTHORIZED
    }

    private ResponseEntity<Void> executePost(LoginDto loginDto) {
        restTemplate.postForEntity(
                localUrl("/email"),
                new HttpEntity(loginDto),
                Void)
    }
}
