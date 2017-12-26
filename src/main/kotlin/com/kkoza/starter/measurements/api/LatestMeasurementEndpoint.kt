package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.MeasurementFacade
import com.kkoza.starter.session.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/measurements/latest")
class LatestMeasurementEndpoint(private val sessionService: SessionService, private val measurementFacade: MeasurementFacade) {

    @GetMapping
    fun getLatestMeasurements(@CookieValue("SESSIONID") sessionId: String): ResponseEntity<LatestMeasurementResponse> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val handleNameToMostRecentMeasurement: List<HandleMeasurement> = measurementFacade.findOneFromAllHandles(userId)
        return ResponseEntity.ok(
                LatestMeasurementResponse(
                        handleNameToMostRecentMeasurement,
                        emptyList()))

    }

}

data class LatestMeasurementResponse(
        val handleMeasurements: List<HandleMeasurement>,
        val nodes: List<NodeMeasurement>
)

class NodeMeasurement
