package com.kkoza.starter.infrastructure.smsclient.textbelt

import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class TextBeltSmsClientConfiguration {

    @Bean
    fun textBeltRestTemplate(
            @Value("\${smsClient.connectionTotal}") maxConnections: Int,
            @Value("\${smsClient.socketTimeout}") socketTimeout: Int,
            @Value("\${smsClient.connectionRequestTimeout}") connectionRequestTimeout: Int,
            @Value("\${smsClient.connectionTimeout}") connectionTimeout: Int
    ): RestTemplate {
        val client = HttpClientBuilder.create()
                .setMaxConnTotal(5)
                .setDefaultRequestConfig(requestConfig(socketTimeout, connectionRequestTimeout, connectionTimeout))
                .build()
        return RestTemplate(HttpComponentsClientHttpRequestFactory(client))
    }

    private fun requestConfig(socketTimeout: Int, connectionRequestTimeout: Int, connectionTimeout: Int): RequestConfig {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build()
    }

}