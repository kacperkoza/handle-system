package com.kkoza.starter.measurements.api

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
    @PostMapping("/measurements")
    fun addMeasurements(
            @RequestBody(required = true) measurementDto: MeasurementDto): ResponseEntity<Void> {
        val handlePosition = when (measurementDto.handlePosition) {
            0 -> HandlePosition.CLOSED
            1 -> HandlePosition.OPEN
            2 -> HandlePosition.REPEALED
            else -> throw RuntimeException("elo")
        }
        val id = measurementFacade.add(measurementDto.let {
            Measurement(
                    null,
                    DateTime.now(),
                    it.handleId,
                    handlePosition,
                    Temperature(it.temperature),
                    Alarm(it.fire, it.burglary, it.frost),

                    SoundLevel(it.soundLevel),
                    it.handleTime)
        })
        return ResponseEntity.created(URI("/measurements/$id")).build()
    }

    @ApiOperation(value = "Get list of user measurements")
    @ApiResponses(ApiResponse(code = 200, message = "Return list with measurement prepared for pagination"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @GetMapping("users/measurements")
    fun getMeasurements(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,
            @ApiParam(value = "Sort data by any of value (case insensitive)", allowableValues = "date_latest, date_oldest, temp_asc, temp_desc, sound_asc, sound_desc")
            @RequestParam(value = "sort", required = false) sort: String?,
            @RequestParam(value = "offset", required = false) offset: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ): ResponseEntity<MeasurementList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val list = measurementFacade.get(userId, sort, offset, limit)
        return ResponseEntity.ok(list)
    }

    @ApiOperation(value = "Delete measurement by ID")
    @ApiResponses(ApiResponse(code = 200, message = "Measurement was deleted if existed"),
            ApiResponse(code = 401, message = "User is not authorized"))
    @DeleteMapping("users/measurements/{id}")
    fun deleteMeasurement(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue(name = "SESSIONID", required = true) sessionId: String,
            @PathVariable("id", required = true) id: String): ResponseEntity<Void> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        measurementFacade.deleteById(id)
        return ResponseEntity.ok(null)
    }


    @ExceptionHandler(InvalidSortTypeException::class)
    fun handleInvalidSortTypeException(ex: InvalidSortTypeException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(InvalidPagingParameterException::class)
    fun handleInvalidPagingParameterException(ex: InvalidPagingParameterException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

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

data class MeasurementDto(
        val handleId: String,
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
        val measurements: List<Measurement>)