package com.kkoza.starter.measurements

import com.kkoza.starter.infrastructure.smsclient.SmsClient

class DangerEventNotifier(private val smsClient: SmsClient) {

    private val smsMessageFactory = NotifierMessageFactory()

    fun notify(measurement: Measurement, phoneNumber: String) {
        measurement.alarm.let {
            if (it.fire) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.fire(measurement.date))
            }
            if (it.burglary) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.burglar(measurement.date))
            }
            if (it.frost) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.frost(measurement.date, measurement.temperature))
            }
        }
    }

}