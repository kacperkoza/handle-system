package com.kkoza.starter.notification

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.infrastructure.smsclient.textbelt.TextBeltSmsClientException
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.settings.SettingsDocument
import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import org.apache.log4j.Logger
import java.lang.invoke.MethodHandles

class NotificationCoordinator(
        private val smsClient: SmsClient,
        private val notifierMessageFactory: NotifierMessageFactory,
        private val deviceFacade: DeviceFacade,
        private val userFacade: UserFacade,
        private val settingsService: SettingsService
) {
    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun notifyIfNecessary(handleMeasurement: HandleMeasurementDocument) {
        val device = deviceFacade.findById(handleMeasurement.handleId) ?: return
        val user = userFacade.findUserById(device.userId)
        user?.let {
            sendAlarmSmsIfNecessary(handleMeasurement, user)
            val settings = settingsService.findByUserId(it.userId!!)
            if (handleMeasurement.temperature < settings.minTemperature) {
                try {
                    smsClient.sendSMS(it.phoneNumber, notifierMessageFactory.belowSet(handleMeasurement.date, handleMeasurement.temperature, settings.minTemperature))
                } catch (ex: TextBeltSmsClientException) {
                    logger.info("User not notified ${it.phoneNumber}")
                }
            }
        }
    }

    private fun sendAlarmSmsIfNecessary(handleMeasurement: HandleMeasurementDocument, user: UserDocument) {
        val messages = notifierMessageFactory.getAllMessages(handleMeasurement)
        messages.forEach {
            smsClient.sendSMS(user.phoneNumber, it)
        }
    }

    fun notifyIfNecessary(nodeMeasurement: NodeMeasurementDocument) {
        val device = deviceFacade.findById(nodeMeasurement.nodeId) ?: return
        val user = userFacade.findUserById(device.userId)
        user?.let {
            val userSettings = settingsService.findByUserId(user.userId!!)

            if (isTemperatureLowerThanInSettings(nodeMeasurement, userSettings)) {
                try {
                    smsClient.sendSMS(it.phoneNumber, notifierMessageFactory.belowSet(nodeMeasurement.date, nodeMeasurement.temperature, userSettings.minTemperature))
                } catch (ex: TextBeltSmsClientException) {
                    logger.info("User not notified ${it.phoneNumber}")
                }
            }

            if (nodeMeasurement.detectedMotion && userSettings.alarmEnabled) {
                try {
                    smsClient.sendSMS(it.phoneNumber, notifierMessageFactory.motionDetected(nodeMeasurement.date, device.name))
                } catch (ex: TextBeltSmsClientException) {
                    logger.info("User not notified ${it.phoneNumber}")
                }
            }
        }
    }

    private fun isTemperatureLowerThanInSettings(nodeMeasurementDocument: NodeMeasurementDocument, userSettings: SettingsDocument): Boolean {
        return nodeMeasurementDocument.temperature < userSettings.minTemperature
    }
}