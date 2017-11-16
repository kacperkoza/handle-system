package com.kkoza.starter.measurements

import org.joda.time.DateTime
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/measurements")
class MeasurementsEndpoint(
        private val measurementFacade: MeasurementFacade
) {


    @GetMapping
    fun getAllMeasurements(): ResponseEntity<MeasurementsResponse> {
        val measurements = measurementFacade.findAll()
        return ResponseEntity.ok(MeasurementsResponse(measurements))
    }
}


data class MeasurementsResponse(
        val measurements: List<Measurement>
)


@Document(collection = Measurement.COLLECTION_NAME)
data class Measurement(
        val id: String? = null,
        val date: DateTime,
        val handlePosition: HandlePosition,
        val temperature: Temperature,
        val alarm: Alarm,
        val soundLevel: SoundLevel,
        val handleTime: Int //wtf?
) {
    companion object {
        const val COLLECTION_NAME = "measurements"
    }
}

data class Alarm(
        val fire: Boolean,
        val burglary: Boolean,
        val frost: Boolean
)

data class SoundLevel(
        val value: Double,
        val unit: String = "dB"
)

data class Temperature(
        val value: Double,
        val unit: String = "C"
)

enum class HandlePosition {
    OPEN, CLOSED, REPEALED
}