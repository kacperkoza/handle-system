package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.*
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.measurements.exception.InvalidSortTypeException
import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/measurements")
class MeasurementsEndpoint(
        private val measurementFacade: MeasurementFacade
) {

    @PostMapping
    fun addMeasurements(@RequestBody(required = true) measurementDto: MeasurementDto): ResponseEntity<Void> {
        val handlePostition = when (measurementDto.handlePosition) {
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
                    handlePostition,
                    Temperature(it.temperature),
                    Alarm(it.fire, it.burglary, it.frost),
                    SoundLevel(it.soundLevel),
                    it.handleTime)
        })
        return ResponseEntity.created(URI("http://localhost:8080/measurements/$id")).build()
    }

    @GetMapping
    fun getMeasurements(
            @RequestParam(value = "sort", required = false) sort: String?,
            @RequestParam(value = "offset", required = false) offset: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ): ResponseEntity<MeasurementList> {
        val list = measurementFacade.get(sort, offset, limit)
        return ResponseEntity.ok(list)
    }

    @DeleteMapping("/{id}")
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
        val date: DateTime,
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