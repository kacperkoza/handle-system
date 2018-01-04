package com.kkoza.starter.handles

import com.kkoza.starter.handles.dto.Temperature
import com.kkoza.starter.infrastructure.smsclient.SmsClient

class DangerEventNotifier(private val smsClient: SmsClient) {

    private val smsMessageFactory = NotifierMessageFactory()

    fun notify(handleMeasurementDocument: HandleMeasurementDocument, phoneNumber: String) {
        handleMeasurementDocument.alarm.let {
            if (it.fire) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.fire(handleMeasurementDocument.date))
            }
            if (it.burglary) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.burglar(handleMeasurementDocument.date))
            }
            if (it.frost) {
                smsClient.sendSMS(phoneNumber, smsMessageFactory.frost(handleMeasurementDocument.date, Temperature(handleMeasurementDocument.temperature)))
            }
        }
    }

}