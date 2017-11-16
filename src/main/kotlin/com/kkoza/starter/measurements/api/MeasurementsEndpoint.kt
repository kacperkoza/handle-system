package com.kkoza.starter.measurements.api

import com.kkoza.starter.measurements.Measurement
import com.kkoza.starter.measurements.MeasurementFacade
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.measurements.exception.InvalidSortTypeException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/measurements")
class MeasurementsEndpoint(
        private val measurementFacade: MeasurementFacade
) {

    @GetMapping
    fun getMeasurements(
            @RequestParam(value = "sort", required = false) sort: String?,
            @RequestParam(value = "offset", required = false) offset: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ): ResponseEntity<MeasurementList> {
        val list = measurementFacade.get(sort, offset, limit)
        return ResponseEntity.ok(list)
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
        fun from(string: String?): SortType {
            string ?: return SortType.DATE_LATEST
            return try {
                SortType.valueOf(string.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw InvalidSortTypeException(string)
            }
        }
    }
}


data class MeasurementList(
        val count: Int,
        val limit: Int?,
        val offset: Int?,
        val measurements: List<Measurement>)