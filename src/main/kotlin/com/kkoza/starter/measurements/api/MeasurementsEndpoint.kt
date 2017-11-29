package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.*
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.measurements.exception.InvalidSortTypeException
import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping
class MeasurementsEndpoint(
        private val measurementFacade: MeasurementFacade
) {

    @PostMapping("/users/measurements")
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
                    it.date,
                    it.handleId,
                    handlePosition,
                    Temperature(it.temperature),
                    Alarm(it.fire, it.burglary, it.frost),
                    SoundLevel(it.soundLevel),
                    it.handleTime)
        })
        return ResponseEntity.created(URI("http://localhost:8080/measurements/$id")).build()
    }

    @GetMapping("users/{userId}/measurements")
    fun getMeasurements(
            @PathVariable(value = "userId", required = true) userId: String,
            @RequestParam(value = "sort", required = false) sort: String?,
            @RequestParam(value = "offset", required = false) offset: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ): ResponseEntity<MeasurementList> {
        val list = measurementFacade.get(userId, sort, offset, limit)
        return ResponseEntity.ok(list)
    }

    @DeleteMapping("users/measurements/{id}")
    fun deleteMeasurement(@PathVariable("id", required = true) id: String): ResponseEntity<Void> {
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

enum class SortType {
    DATE_LATEST,
    DATE_OLDEST,
    TEMPERATURE_ASCENDING,
    TEMPERATURE_DESCENDING,
    SOUND_LEVEL_ASCENDING,
    SOUND_LEVEL_DESCENDING;

    companion object {
        fun from(source: String?): SortType {
            source ?: return SortType.DATE_LATEST
            return try {
                SortType.valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw InvalidSortTypeException(source)
            }
        }
    }
}

data class MeasurementDto(
        val date: DateTime = DateTime.now(),
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