package com.kkoza.starter.settings

import org.springframework.stereotype.Service

@Service
class SettingsService(private val settingsRepository: SettingsRepository) {

    fun createDefaultSettings(userId: String, temperature: Double = 25.0, motionAlarm: Boolean = false) {
        val settings = SettingsDocument(userId, temperature, motionAlarm)
        settingsRepository.save(settings)
    }

    fun setTemperature(userId: String, temperature: Double) {
        settingsRepository.updateTemperature(userId, temperature)
    }

    fun updateSettings(settingsDocument: SettingsDocument) {
        settingsRepository.save(settingsDocument)
    }

    fun findByUserId(userId: String): SettingsDocument {
        return settingsRepository.findByUserId(userId)
    }

    fun setAlarm(userId: String, enabled: Boolean) {
        if (enabled) enableMotionAlarm(userId) else disableMotionAlarm(userId)
    }

    private fun enableMotionAlarm(userId: String) {
        settingsRepository.enableMotionAlarm(userId)
    }

    private fun disableMotionAlarm(userId: String) {
        settingsRepository.disableMotionAlarm(userId)
    }

}