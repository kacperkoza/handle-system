package com.kkoza.starter.handles.api

import com.kkoza.starter.handles.*
import com.kkoza.starter.handles.dto.HandleMeasurementDto
import com.kkoza.starter.handles.dto.MeasurementList
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.handles.exception.InvalidSortTypeException
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
@Api(value = "Information about user's handles", description = "Add, get, delete user's handles")
class HandleMeasurementEndpoint(
        private val measurementFacade: MeasurementFacade,
        private val sessionService: SessionService
) {

    @ApiOperation(value = "Used to create new measurement")
    @ApiResponse(code = 201, message = "Successfully created new measurement. See 'Location' in headers")
    @PostMapping("/measurements/handles")
    fun addMeasurements(
            @RequestBody(required = true) measurementDto: HandleMeasurementDto): ResponseEntity<Void> {
        val handlePosition = when (measurementDto.handlePosition) {
            0 -> HandlePosition.CLOSED
            1 -> HandlePosition.OPEN
            2 -> HandlePosition.REPEALED
            else -> throw RuntimeException("elo")
        }
        val id = measurementFacade.addHandleMeasurement(measurementDto.let {
            HandleMeasurementDocument(
                    null,
                    DateTime.now().plusHours(1),
                    it.deviceId,
                    handlePosition,
                    it.temperature,
                    Alarm(it.fire, it.burglary, it.frost),
                    it.soundLevel,
                    it.handleTime)
        })
        return ResponseEntity.created(URI("/measurements/handles/$id")).build()
    }

    @ApiOperation(value = "Get list of user handles")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with measurement prepared for pagination"),
            ApiResponse(code = 400, message = "Provided query parameters are invalid. See message in response body"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @GetMapping("users/measurements/handles")
    fun getMeasurements(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,

            @ApiParam(value = "Sort data by any of value (case insensitive)", allowableValues = "date_latest, date_oldest, temp_asc, temp_desc, sound_asc, sound_desc")
            @RequestParam(value = "sort", required = false) sort: String?,

            @RequestParam(value = "offset", required = false) offset: Int?,

            @RequestParam(value = "limit", required = false) limit: Int?,

            @ApiParam(value = "Alarms to filter (you can select multiple", allowableValues = "fire, alarm, frost")
            @RequestParam(value = "alarms", required = false) alarms: List<String>?,

            @ApiParam(value = "Ids of devices to filter (you can use multiple)")
            @RequestParam(value = "handles", required = false) handles: List<String>?,

            @ApiParam(value = "Starting date in yyyy-MM-dd HH:mm pattern (can't be after 'endDate')", required = false)
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") startDate: DateTime?,

            @ApiParam(value = "Ending date in yyyy-MM-dd HH:mm patterng", required = false)
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") endDate: DateTime?
    ): ResponseEntity<MeasurementList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val sortType = HandleSortType.from(sort)
        val alarmFilters = alarms?.map { AlarmFilter.from(it) }
        val list = measurementFacade.getHandleMeasurements(userId, sortType, offset, limit, alarmFilters, handles, startDate, endDate)
        return ResponseEntity.ok(list)
    }

    @ApiOperation(value = "Delete measurement by ID")
    @ApiResponses(ApiResponse(code = 200, message = "Handle measurement was deleted if existed"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @DeleteMapping("users/measurements/handles/{id}")
    fun deleteMeasurement(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,
            @PathVariable("id", required = true) id: String): ResponseEntity<Void> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        measurementFacade.deleteHandleMeasurementById(id)
        return ResponseEntity.ok(null)
    }

    @ExceptionHandler(InvalidSortTypeException::class)
    fun handleSortEx(ex: InvalidSortTypeException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidPagingParameterException::class)
    fun handlePagingEx(ex: InvalidPagingParameterException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidAlarmFilterException::class)
    fun handleAlarmFilterEx(ex: InvalidAlarmFilterException) = ResponseEntity.badRequest().body(ex.message)

    @ExceptionHandler(InvalidHandleMeasurementException::class)
    fun handle(ex: InvalidHandleMeasurementException) = ResponseEntity.badRequest().body(ex.message)

    @ExceptionHandler(InvalidDateFiltersException::class)
    fun handle(ex: InvalidDateFiltersException) = ResponseEntity.unprocessableEntity().body(ex.message)

    @ExceptionHandler(InvalidSessionException::class)
    fun handle(ex: InvalidSessionException) = ResponseEntity(ex.message!!, HttpStatus.UNAUTHORIZED)

}

enum class HandleSortType {
    DATE_LATEST,
    DATE_OLDEST,
    TEMP_ASC,
    TEMP_DESC,
    SOUND_ASC,
    SOUND_DESC;

    companion object {
        fun from(source: String?): HandleSortType {
            source ?: return HandleSortType.DATE_LATEST
            return try {
                HandleSortType.valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw InvalidSortTypeException(source, HandleSortType.values().joinToString(separator = ", ", prefix = "[", postfix = "]"))
            }
        }
    }

}

data class AlarmFilter private constructor(
        val fieldName: String,
        val value: Boolean
) {

    companion object {
        private val FIRE = AlarmFilter("alarm.fire", true)
        private val FROST = AlarmFilter("alarm.frost", true)
        private val BURGLARY = AlarmFilter("alarm.burglary", true)

        val alarmNames = mapOf(
                "fire" to FIRE,
                "frost" to FROST,
                "burglary" to BURGLARY)

        fun from(source: String): AlarmFilter = alarmNames[source] ?: throw InvalidAlarmFilterException(source)
    }
}

class InvalidAlarmFilterException(source: String) : RuntimeException("Wrong alarm filter value - $source." +
        " filter must be in values (case insensitive) ${AlarmFilter.alarmNames.keys.joinToString(", ", "[", "]")}")