package com.kkoza.starter.infrastructure.smsclient

interface SmsClient {

    fun sendSMS(phoneNumber: String, message: String)

}