package com.kkoza.starter.measurements.api

import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.measurements.*
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.measurements.exception.InvalidSortTypeException
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Api(value = "Information about user's measurements", description = "Add, get, delete user's measurements")
class MeasurementsEndpoint(
        private val measurementFacade: MeasurementFacade,
        private val sessionService: SessionService
) {

    @ApiOperation(value = "Used to create new measurement")
    @ApiResponse(code = 201, message = "Successfully created new measurement. See 'Location' in headers")
    @PostMapping("/measurements/handles")
    fun addMeasurements(
            @RequestBody(required = true) measurementDto: MeasurementDto): ResponseEntity<Void> {
        val handlePosition = when (measurementDto.handlePosition) {
            0 -> HandlePosition.CLOSED
            1 -> HandlePosition.OPEN
            2 -> HandlePosition.REPEALED
            else -> throw RuntimeException("elo")
        }
        val id = measurementFacade.add(measurementDto.let {
            HandleMeasurementDocument(
                    null,
                    DateTime.now(),
                    it.deviceId,
                    handlePosition,
                    Temperature(it.temperature),
                    Alarm(it.fire, it.burglary, it.frost),

                    SoundLevel(it.soundLevel),
                    it.handleTime)
        })
        return ResponseEntity.created(URI("/measurements/handles/$id")).build()
    }

    @ApiOperation(value = "Get list of user measurements")
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
            @ApiParam(value = "Ids of devices to filter (you can select multiple)")
            @RequestParam(value = "alarms", required = false) alarms: List<String>?,
            @ApiParam(value = "Alarms to filter (you can select multiple", allowableValues = "fire, alarm, frost")
            @RequestParam(value = "handles", required = false) handles: List<String>?
    ): ResponseEntity<MeasurementList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val sortType = MeasurementSortType.from(sort)
        val alarmFilters = alarms?.map { AlarmFilter.from(it) }
        val list = measurementFacade.get(userId, sortType, offset, limit, alarmFilters, handles)
        return ResponseEntity.ok(list)
    }

    @ApiOperation(value = "Delete measurement by ID")
    @ApiResponses(ApiResponse(code = 200, message = "HandleMeasurementDocument was deleted if existed"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @DeleteMapping("users/measurements/handles/{id}")
    fun deleteMeasurement(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,
            @PathVariable("id", required = true) id: String): ResponseEntity<Void> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        measurementFacade.deleteById(id)
        return ResponseEntity.ok(null)
    }

    @ExceptionHandler(InvalidSortTypeException::class)
    fun handleSortEx(ex: InvalidSortTypeException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidPagingParameterException::class)
    fun handlePagingEx(ex: InvalidPagingParameterException) = ResponseEntity.badRequest().body(ex.message)


    @ExceptionHandler(InvalidAlarmFilterException::class)
    fun handleAlarmFilterEx(ex: InvalidAlarmFilterException) = ResponseEntity.badRequest().body(ex.message)

}

enum class MeasurementSortType {
    DATE_LATEST,
    DATE_OLDEST,
    TEMP_ASC,
    TEMP_DESC,
    SOUND_ASC,
    SOUND_DESC;

    companion object {
        fun from(source: String?): MeasurementSortType {
            source ?: return MeasurementSortType.DATE_LATEST
            return try {
                MeasurementSortType.valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw InvalidSortTypeException(source)
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


data class MeasurementDto(
        val deviceId: String,
        val handlePosition: Int,
        val temperature: Double,
        val fire: Boolean,
        val burglary: Boolean,
        val frost: Boolean,
        val soundLevel: Double,
        val handleTime: Int
)


data class MeasurementList(
        val count: Int,
        val limit: Int?,
        val offset: Int?,
        val measurements: List<Measurement>,
        val handles: List<DeviceDto>)

data class Measurement(
        val id: String? = null,
        val date: DateTime,
        val handleName: String,
        val handlePosition: HandlePosition,
        val temperature: Temperature,
        val alarm: Alarm,
        val soundLevel: SoundLevel,
        val handleTime: Int //wtf?
)