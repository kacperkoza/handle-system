package com.kkoza.starter.infrastructure.smsclient.fake

import com.kkoza.starter.infrastructure.smsclient.SmsClient
import org.apache.log4j.Logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles

@Component
@ConditionalOnProperty(value = "smsClient.client", havingValue = "fake")
class FakeSmsClient : SmsClient {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    override fun sendSMS(phoneNumber: String, message: String) {
        logger.info("Fake sms sent to: $phoneNumber with message: $message")
    }
}