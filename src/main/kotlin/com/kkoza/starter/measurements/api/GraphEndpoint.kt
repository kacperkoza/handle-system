package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.MeasurementFacade
import com.kkoza.starter.measurements.exception.IllegalQueryDateException
import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/graphs")
@Api(value = "Data for graphs", description = "Get data to create graphs with sound level / temperature")
class GraphEndpoint(
        private val sessionService: SessionService,
        private val measurementFacade: MeasurementFacade
) {

    @ApiOperation(value = "Query for graph data")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with data for queried field and dates"),
            ApiResponse(code = 400, message = "Invalid fieldName for filtering"),
            ApiResponse(code = 401, message = "Invalid or expired cookie session"),
            ApiResponse(code = 422, message = "Starting date is after ending date"))
    @GetMapping("/measurements")
    fun getGraphData(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,

            @ApiParam(value = "Starting date in yyyy-MM-dd HH:mm pattern (can't be after 'endDate')", required = true)
            @RequestParam("startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,

            @ApiParam(value = "Ending date in yyyy-MM-dd HH:mm pattern", required = true)
            @RequestParam("endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?,

            @ApiParam(value = "Query for given field in measurement (case insensitive)", allowableValues = "temperature, sound_level")
            @RequestParam("fieldName", required = true) fieldName: String,

            @ApiParam(value = "Handle which data you want to get")
            @RequestParam("handleId", required = true) handleId: String
    ): ResponseEntity<ItemsDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val fieldFilter = FieldFilter.fromString(fieldName)
        val list = measurementFacade.getGraphDataList(startDate, endDate, fieldFilter, handleId)
        return ResponseEntity.ok(list)
    }

    @ExceptionHandler(UnknownFilterException::class)
    fun handle(ex: UnknownFilterException) = ResponseEntity.badRequest().body(ex.message)!!

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException): ResponseEntity<Void> = ResponseEntity(null, HttpStatus.UNAUTHORIZED)!!

    @ExceptionHandler(IllegalQueryDateException::class)
    fun handle(ex: IllegalQueryDateException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

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

class UnknownFilterException(source: String) : RuntimeException("Field name =$source is wrong. Values must be in ${FieldFilter.values().joinToString(", ", "[", "]")} case insensitive")

data class ItemsDto(
        val data: List<GraphItem>
)


data class GraphItem(
        val date: DateTime,
        val value: Double
)