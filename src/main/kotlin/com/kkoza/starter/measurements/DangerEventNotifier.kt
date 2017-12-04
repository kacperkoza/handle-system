package com.kkoza.starter.measurements

import com.kkoza.starter.infrastructure.smsclient.SmsClient

class DangerEventNotifier(private val smsClient: SmsClient) {

    private val smsMessageFactory = NotifierMessageFactory()

    fun notify(measurementDocument: MeasurementDocument, phoneNumber: String) {
        measurementDocument.alarm.let {
            if (it.fire) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.fire(measurementDocument.date))
            }
            if (it.burglary) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.burglar(measurementDocument.date))
            }
            if (it.frost) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.frost(measurementDocument.date, measurementDocument.temperature))
            }
        }
    }

}