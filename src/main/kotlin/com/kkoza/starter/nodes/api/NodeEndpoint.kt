package com.kkoza.starter.nodes.api

import com.kkoza.starter.handles.InvalidDateFiltersException
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.handles.exception.InvalidSortTypeException
import com.kkoza.starter.nodes.InvalidNodeMeasurementException
import com.kkoza.starter.nodes.NodeFacade
import com.kkoza.starter.nodes.NodeMeasurementDocument
import com.kkoza.starter.nodes.NodeSortType
import com.kkoza.starter.nodes.dto.NodeMeasurementDto
import com.kkoza.starter.nodes.dto.NodeMeasurementList
import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Api(value = "Information about user's node measurements", description = "Add, get, delete user's node measurements")
class NodeEndpoint(
        private val nodeFacade: NodeFacade,
        private val sessionService: SessionService
) {

    @ApiOperation(value = "Use to create new node measurement")
    @ApiResponse(code = 201, message = "Successfully created new node measurement. See 'Location' in headers")
    @PostMapping("/measurements/nodes")
    fun addMeasurements(@RequestBody(required = true) nodeMeasurementDto: NodeMeasurementDto): ResponseEntity<Void> {
        val id = nodeFacade.addNodeMeasurement(nodeMeasurementDto.let {
            NodeMeasurementDocument(
                    null,
                    DateTime.now(),
                    it.deviceId,
                    it.temperature,
                    it.humidity,
                    it.lightIntensity,
                    it.motion,
                    it.carbonDioxide)
        })
        return ResponseEntity.created(URI("/measurements/nodes/$id")).build()
    }

    @ApiOperation(value = "Get list of user node measurements")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with node measurement prepared for pagination"),
            ApiResponse(code = 400, message = "Provided query parameters are invalid. See message in response body"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @GetMapping("users/measurements/nodes")
    fun getMeasurements(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,

            @ApiParam(value = "Sort data by any of value (case insensitive)", allowableValues = "date_latest, date_oldest, temp_asc, temp_desc, sound_asc, sound_desc, hum_asc, hum_desc, light_asc, light_desc, carbon_asc, carbon_desc")
            @RequestParam(value = "sort", required = false) sort: String?,

            @RequestParam(value = "offset", required = false) offset: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?,

            @ApiParam(value = "Ids of devices to filter (you can select multiple)")
            @RequestParam(value = "nodes", required = false) nodes: List<String>?,

            @ApiParam(value = "Filter by field set to true", allowableValues = "motion")
            @RequestParam(value="filters", required = false) filters: List<String>?,

            @ApiParam(value = "Starting date in yyyy-MM-dd HH:mm pattern (can't be after 'endDate')", required = false)
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,

            @ApiParam(value = "Ending date in yyyy-MM-dd HH:mm patterng", required = false)
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?
    ): ResponseEntity<NodeMeasurementList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val sortType = NodeSortType.from(sort)
        val fieldFilters = filters?.map { NodeFilter.from(it) }
        val list = nodeFacade.getNodeMeasurement(userId, sortType, offset, limit, nodes, fieldFilters, startDate, endDate)
        return ResponseEntity.ok(list)
    }

    @ApiOperation(value = "Delete node measurement by ID")
    @ApiResponses(ApiResponse(code = 200, message = "Node measurement was deleted if existed"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @DeleteMapping("users/measurements/nodes/{id}")
    fun deleteMeasurement(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,
            @PathVariable("id", required = true) id: String): ResponseEntity<Void> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        nodeFacade.deleteNodeMeasurementById(id)
        return ResponseEntity.ok(null)
    }

    @ExceptionHandler(InvalidSortTypeException::class)
    fun handle(ex: InvalidSortTypeException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidPagingParameterException::class)
    fun handle(ex: InvalidPagingParameterException) = ResponseEntity.badRequest().body(ex.message)

    @ExceptionHandler(InvalidNodeMeasurementException::class)
    fun handle(ex: InvalidNodeMeasurementException) = ResponseEntity.badRequest().body(ex.message)

    @ExceptionHandler(InvalidDateFiltersException::class)
    fun handle(ex: InvalidDateFiltersException) = ResponseEntity.unprocessableEntity().body(ex.message)

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException) = ResponseEntity(ex.message!!, HttpStatus.UNAUTHORIZED)

}

data class NodeFilter private constructor(
        val fieldName: String,
        val value: Boolean
) {

    companion object {
        private val MOTION = NodeFilter("detectedMotion", true)

        val filters = mapOf("motion" to MOTION)

        fun from(source: String): NodeFilter = filters[source] ?: throw InvalidNodeFilterException(source)
    }

}

class InvalidNodeFilterException(source: String) : RuntimeException("Wrong alarm filter value - $source." +
        " filter must be in values (case insensitive) ${NodeFilter.filters.keys.joinToString(", ", "[", "]")}")



