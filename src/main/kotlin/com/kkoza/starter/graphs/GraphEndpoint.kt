package com.kkoza.starter.graphs

import com.kkoza.starter.handles.exception.IllegalQueryDateException
import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/graphs")
@Api(value = "Data for graphs", description = "Get data for creating graphs")
class GraphEndpoint(
        private val sessionService: SessionService,
        private val graphFacade: GraphFacade
) {

    @ApiOperation(value = "Query graph data for any handle")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with data for queried field and dates"),
            ApiResponse(code = 400, message = "Invalid fieldName for filtering"),
            ApiResponse(code = 401, message = "Invalid or expired cookie session"),
            ApiResponse(code = 422, message = "Starting date is after ending date"))
    @GetMapping("/handles")
    fun getHandleData(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,

            @ApiParam(value = "Starting date in yyyy-MM-dd HH:mm pattern (can't be after 'endDate')", required = false)
            @RequestParam("startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,

            @ApiParam(value = "Ending date in yyyy-MM-dd HH:mm pattern", required = false)
            @RequestParam("endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?,

            @ApiParam(value = "Query for given field in measurement (case insensitive)", allowableValues = "temperature, sound_level", required = true)
            @RequestParam("fieldName", required = true) fieldName: String,

            @ApiParam(value = "Handle which data you want to get", required = true)
            @RequestParam("handleId", required = true) handleId: String
    ): ResponseEntity<ItemsDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val fieldFilter = HandleFieldFilter.fromString(fieldName)
        val list = graphFacade.getGraphDataFromHandle(startDate, endDate, fieldFilter, handleId)
        return ResponseEntity.ok(list)
    }

    @ApiOperation(value = "Query for node graph data")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with data for queried field and dates"),
            ApiResponse(code = 400, message = "Invalid fieldName for filtering"),
            ApiResponse(code = 401, message = "Invalid or expired cookie session"),
            ApiResponse(code = 422, message = "Starting date is after ending date"))
    @GetMapping("/nodes")
    fun getNodeData(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,

            @ApiParam(value = "Starting date in yyyy-MM-dd HH:mm pattern (can't be after 'endDate')", required = false)
            @RequestParam("startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,

            @ApiParam(value = "Ending date in yyyy-MM-dd HH:mm pattern", required = false)
            @RequestParam("endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?,

            @ApiParam(value = "Query for given field in measurement (case insensitive)", allowableValues = "temperature, humidity, carbon, light_intensity", required = true)
            @RequestParam("fieldName", required = true) fieldName: String,

            @ApiParam(value = "Node which data you want to get", required = true)
            @RequestParam("nodeId", required = true) nodeId: String
    ): ResponseEntity<ItemsDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val fieldFilter = NodeFieldFilter.fromString(fieldName)
        val list = graphFacade.getGraphDataFromNode(startDate, endDate, fieldFilter, nodeId)
        return ResponseEntity.ok(list)
    }

    @ExceptionHandler(UnknownFilterException::class)
    fun handle(ex: UnknownFilterException) = ResponseEntity.badRequest().body(ex.message)!!

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException): ResponseEntity<Void> = ResponseEntity(null, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(IllegalQueryDateException::class)
    fun handle(ex: IllegalQueryDateException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

}

enum class HandleFieldFilter {

    TEMPERATURE, SOUND_LEVEL;

    companion object {
        fun fromString(source: String): HandleFieldFilter {
            try {
                return valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw UnknownFilterException(source, HandleFieldFilter.values().map { it.name })
            }
        }
    }
}

enum class NodeFieldFilter {

    TEMPERATURE, HUMIDITY, CARBON, LIGHT_INTENSITY;

    companion object {
        fun fromString(source: String): NodeFieldFilter {
            try {
                return NodeFieldFilter.valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw UnknownFilterException(source, NodeFieldFilter.values().map { it.name })
            }
        }
    }
}

class UnknownFilterException(source: String, availableValues: List<String>) : RuntimeException("Field name =$source is wrong. Values must be in ${availableValues.joinToString(", ", "[", "]")} case insensitive")

data class ItemsDto(
        val data: List<GraphItem>
)


data class GraphItem(
        val date: DateTime,
        val value: Double
)