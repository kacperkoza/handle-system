package com.kkoza.starter.notification

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.handles.dto.Temperature
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.user.UserFacade

class NotificationService(
        private val smsClient: SmsClient,
        private val notifierMessageFactory: NotifierMessageFactory,
        private val deviceFacade: DeviceFacade,
        private val userFacade: UserFacade,
        private val settingsService: SettingsService
) {

    fun notifyIfNecessary(handleMeasurementDocument: HandleMeasurementDocument) {
        val device = deviceFacade.findById(handleMeasurementDocument.handleId) ?: return
        val owner = userFacade.findUserById(device.userId) ?: return
        val userSettings = settingsService.findByUserId(owner.userId!!)
        checkAlarm(handleMeasurementDocument, owner.phoneNumber)
        checkTemperature(handleMeasurementDocument.temperature, userSettings.minTemperature)
    }

    private fun checkAlarm(measurementDocument: HandleMeasurementDocument, phoneNumber: String) {
        measurementDocument.alarm.let {
            if (it.fire) {
                smsClient.sendSMS(phoneNumber, notifierMessageFactory.fire(measurementDocument.date))
            }
            if (it.burglary) {
                smsClient.sendSMS(phoneNumber, notifierMessageFactory.burglar(measurementDocument.date))
            }
            if (it.frost) {
                smsClient.sendSMS(phoneNumber, notifierMessageFactory.frost(measurementDocument.date, Temperature(measurementDocument.temperature)))
            }
        }
    }

    private fun checkTemperature(temperature: Double, userTemperature: Double) {
        if (temperature < userTemperature) {
//            smsClient.sendSMS()
        }


    }
}