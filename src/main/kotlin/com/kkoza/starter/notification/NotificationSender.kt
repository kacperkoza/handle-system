package com.kkoza.starter.notification

import com.kkoza.starter.infrastructure.smsclient.SmsClient
import org.apache.log4j.Logger
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles

@Component
class NotificationSender(private val smsClient: SmsClient) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun sendSms(phoneNumber: String, message: String) {
        smsClient.sendSMS(phoneNumber, message)
    }

}