package com.kkoza.starter.nodes.api

import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.Temperature
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.handles.exception.InvalidSortTypeException
import com.kkoza.starter.nodes.*
import com.kkoza.starter.nodes.NodeMeasurementList
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.joda.time.DateTime
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
                    Temperature(it.temperature),
                    Humidity(it.humidity),
                    LightIntensity(it.lightIntensity),
                    it.motion,
                    CarbonDioxide(it.carbonDioxide))
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
            @RequestParam(value="filters", required = false) filters: List<String>
    ): ResponseEntity<NodeMeasurementList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val sortType = NodeSortType.from(sort)
        val fieldFilters = filters.map { NodeFilter.from(it) }
        val list = nodeFacade.getNodeMeasurement(userId, sortType, offset, limit, nodes, fieldFilters)
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
    fun handleSortEx(ex: InvalidSortTypeException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidPagingParameterException::class)
    fun handlePagingEx(ex: InvalidPagingParameterException) = ResponseEntity.badRequest().body(ex.message)


}

data class NodeFilter private constructor(
        val fieldName: String,
        val value: Boolean
) {

    companion object {
        private val MOTION = NodeFilter("alarm.motion", true)

        val filters = mapOf("motion" to MOTION)

        fun from(source: String): NodeFilter = filters[source] ?: throw InvalidNodeFilterException(source)
    }
}

class InvalidNodeFilterException(source: String) : RuntimeException("Wrong alarm filter value - $source." +
        " filter must be in values (case insensitive) ${NodeFilter.filters.keys.joinToString(", ", "[", "]")}")


data class NodeMeasurementDto(
        val deviceId: String,
        val temperature: Double,
        val lightIntensity: Double,
        val humidity: Double,
        val motion: Boolean,
        val carbonDioxide: Double
)


data class NodeMeasurementList(
        val count: Int,
        val limit: Int?,
        val offset: Int?,
        val nodesMeasurements: List<NodeMeasurement>,
        val handles: List<DeviceDto>
)

data class NodeMeasurement(
        val id: String? = null,
        val date: DateTime,
        val nodeId: String,
        val temperature: Temperature,
        val humidity: Humidity,
        val carbonDioxide: CarbonDioxide,
        val motion: Boolean
)