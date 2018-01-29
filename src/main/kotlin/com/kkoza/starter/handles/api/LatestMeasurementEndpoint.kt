package com.kkoza.starter.handles.api

import com.kkoza.starter.handles.MeasurementFacade
import com.kkoza.starter.handles.dto.HandleMeasurement
import com.kkoza.starter.nodes.NodeFacade
import com.kkoza.starter.nodes.dto.NodeMeasurement
import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.SessionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/measurements/latest")
class LatestMeasurementEndpoint(
        private val sessionService: SessionService,
        private val measurementFacade: MeasurementFacade,
        private val nodeFacade: NodeFacade) {

    @GetMapping
    fun getLatestMeasurements(@CookieValue("SESSIONID") sessionId: String): ResponseEntity<LatestMeasurementResponse> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val mostRecentHandleMeasurements: List<HandleMeasurement> = measurementFacade.findOneFromAllDevices(userId)
        val mostRecentNodeMeasurements = nodeFacade.findOneFromAllNodes(userId)
        return ResponseEntity.ok(
                LatestMeasurementResponse(
                        mostRecentHandleMeasurements,
                        mostRecentNodeMeasurements))

    }

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException) = ResponseEntity(ex.message!!, HttpStatus.UNAUTHORIZED)

}

data class LatestMeasurementResponse(
        val handles: List<HandleMeasurement>,
        val nodes: List<NodeMeasurement>
)

