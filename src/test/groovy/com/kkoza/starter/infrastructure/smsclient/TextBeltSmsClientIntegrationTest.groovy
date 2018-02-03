package com.kkoza.starter.infrastructure.smsclient

import com.kkoza.starter.BaseIntegrationTest
import com.kkoza.starter.infrastructure.smsclient.textbelt.Sms
import com.kkoza.starter.infrastructure.smsclient.textbelt.TextBeltSmsClientException
import org.springframework.beans.factory.annotation.Autowired

class TextBeltSmsClientIntegrationTest extends BaseIntegrationTest {

    @Autowired
    SmsClient smsClient

    def 'should successfully send SMS'() {
        given:
        stubSmsClient(200, new Sms('+48123456789', 'message'))

        when:
        smsClient.sendSMS('123456789', 'message')

        then:
        true
    }

    def 'should throw exception when server responds with client/server error'() {
        given:
        stubSmsClient(400, new Sms('+48123456789', 'message'))

        when:
        smsClient.sendSMS('123456789', "message")

        then:
        thrown(TextBeltSmsClientException)

        where:
        statucCode << [400, 404, 500, 503]
    }


}
