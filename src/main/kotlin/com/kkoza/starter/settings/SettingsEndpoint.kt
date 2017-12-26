package com.kkoza.starter.settings

import com.kkoza.starter.session.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/settings")
class SettingsEndpoint(private val settingsService: SettingsService, private val sessionService: SessionService) {

    @GetMapping
    fun getSettings(@CookieValue("SESSIONID") sessionId: String): ResponseEntity<SettingsResponse> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val settings = settingsService.findByUserId(userId)
        return ResponseEntity.ok(
                SettingsResponse(
                        SettingsDto(settings.minTemperature, settings.alarmEnabled)))
    }

    @PutMapping
    fun getSettings(@CookieValue("SESSIONID") sessionId: String,
                    @RequestBody settings: SettingsDto): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        settingsService.updateSettings(SettingsDocument(userId, settings.minTemperature, settings.alarmEnabled))
        return ResponseEntity.ok(null)
    }

    @PostMapping("alarm")
    fun updateAlarm(@CookieValue("SESSIONID") sessionId: String,
                    @RequestBody alarmDto: AlarmDto): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        settingsService.setAlarm(userId, alarmDto.enabled)
        return ResponseEntity.ok(null)
    }

}


data class SettingsDto(
        val minTemperature: Double,
        val alarmEnabled: Boolean
)

data class SettingsResponse(
        val settings: SettingsDto
)

data class AlarmDto(
        val enabled: Boolean
)

