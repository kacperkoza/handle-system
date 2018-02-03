package com.kkoza.starter.infrastructure.smsclient.textbelt

import com.kkoza.starter.infrastructure.smsclient.SmsClient
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.lang.invoke.MethodHandles

@ConditionalOnProperty(value = "smsClient.client", havingValue = "textbelt")
@Component
class TextBeltSmsClient(
        private val textBeltRestTemplate: RestTemplate,
        @Value("\${smsClient.url}") val url: String) : SmsClient {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
        private const val POLISH_PREFIX = "+48"
    }

    override fun sendSMS(phoneNumber: String, message: String) {
        val sms = Sms("$POLISH_PREFIX$phoneNumber", message)
        logger.info("Try to send " +
                "SMS = $sms")
        logger.info("url $url")
        try {
            val report = textBeltRestTemplate.exchange(url, HttpMethod.POST, HttpEntity(sms), MessageReport::class.java).body
            logger.info("Successfully sent to ${sms.phone} with report $report")
        } catch (ex: HttpClientErrorException) {
            logger.error("Problem with send SMS - client error", ex)
            throw TextBeltSmsClientException(sms, ex)
        } catch (ex: Exception) {
            logger.error("Problem with TextBelt", ex)
            throw TextBeltSmsClientException(sms, ex)
        }
    }

}

data class Sms(
        val phone: String,
        val message: String,
        val key: String = "textbelt"
)

data class MessageReport(
        val sending: Boolean,
        val quotaRemaining: Int,
        val textId: Int
)