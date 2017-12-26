package com.kkoza.starter.settings

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.session.SessionDocument
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class SettingsEndpointTest extends BaseIntegrationTest {


    def setup() {
        save(new SessionDocument('session-id', 'user-id', DateTime.now().plusMinutes(10)))
        save(new SettingsDocument('user-id', 10.0d, false))
    }


    def '[GET] should return user settings'() {
        when:
        def response = restTemplate.exchange(localUrl('/users/settings'), HttpMethod.GET, new HttpEntity<Object>(getHeaders()), SettingsResponse)

        then:
        with(response.body.settings) {
            minTemperature == 10.0d
            alarmEnabled == false
            userId == 'user-id'
        }
    }

    def '[PUT] should override users settings'() {
        given:
        SettingsDto settingsDto = new SettingsDto('user-id', 20.0d, true)

        when:
        restTemplate.exchange(localUrl('/users/settings/user-id'), HttpMethod.PUT, new HttpEntity<Object>(settingsDto, getHeaders()), String)
        def settings = findOne()

        then:
        with(settings) {
            userId == 'user-id'
            minTemperature == 20.0d
            alarmEnabled == true
        }
    }

    def '[POST] should enable or disable user\'s alarm'() {
        given:
        AlarmDto alarmDto = new AlarmDto(enabled)

        when:
        restTemplate.exchange(localUrl('/users/settings/alarm'), HttpMethod.POST, new HttpEntity<Object>(alarmDto, getHeaders()), Void)
        def settings = findOne()

        then:
        settings.alarmEnabled == enabled

        where:
        enabled << [true, false]

    }

    private SettingsDocument findOne() {
        mongoTemplate.findOne(new Query(), SettingsDocument)
    }


    def getHeaders() {
        def headers = new HttpHeaders()
        headers.set("Cookie", "SESSIONID=session-id")
        return headers
    }
}
