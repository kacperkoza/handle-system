package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.MeasurementFacade
import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.SessionService
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/graphs")
class GraphEndpoint(
        private val sessionService: SessionService,
        private val measurementFacade: MeasurementFacade
) {

    @GetMapping("/measurements")
    fun getGraphData(@CookieValue("SESSIONID", required = true) sessionId: String,
                     @RequestParam("startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,
                     @RequestParam("endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?,
                     @RequestParam("fieldName", required = true) fieldName: String,
                     @RequestParam("handleId", required = true) handleId: String): ResponseEntity<ItemsDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val fieldFilter = FieldFilter.fromString(fieldName)
        val list = measurementFacade.getGraphDataList(startDate, endDate, fieldFilter, handleId)
        return ResponseEntity.ok(list)
    }


    @ExceptionHandler(UnknownFilterException::class)
    fun handle(ex: UnknownFilterException) = ResponseEntity.badRequest().body(ex.message)!!

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException): ResponseEntity<Void> = ResponseEntity(null,HttpStatus.UNAUTHORIZED)

}

enum class FieldFilter {

    TEMPERATURE, SOUND_LEVEL;

    companion object {
        fun fromString(source: String): FieldFilter {
            try {
                return valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw UnknownFilterException(source)
            }
        }
    }
}

class UnknownFilterException(source: String) : RuntimeException("Filter values must be in ${FieldFilter.values().joinToString(", ", "[", "]")}")

data class ItemsDto(
        val data: List<GraphItem>
)


data class GraphItem(
        val date: DateTime,
        val value: Double
)