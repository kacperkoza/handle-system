package com.kkoza.starter.handles

import com.kkoza.starter.handles.dto.HandleMeasurement
import com.kkoza.starter.handles.dto.SoundLevel
import com.kkoza.starter.handles.dto.Temperature
import org.joda.time.DateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = HandleMeasurementDocument.COLLECTION_NAME)
data class HandleMeasurementDocument(
        @Id
        @Field(ID)
        val id: String? = null,

        @CreatedDate
        @Field(DATE)
        val date: DateTime,

        @Field(HANDLE_ID)
        val handleId: String,

        @Field(HANDLE_POSITION)
        val handlePosition: HandlePosition,

        @Field(TEMPERATURE)
        val temperature: Double,

        @Field(ALARM)
        val alarm: Alarm,

        @Field(SOUND_LEVEL)
        val soundLevel: Double,

        @Field(HANDLE_TIME)
        val handleTime: Int //wtf?
) {
    companion object {
        const val COLLECTION_NAME = "handle_measurements"

        const val ID = "_id"
        const val DATE = "date"
        const val HANDLE_ID = "handle_id"
        const val HANDLE_POSITION = "handle_position"
        const val TEMPERATURE = "temperature"
        const val ALARM = "alarm"
        const val SOUND_LEVEL = "sound_level"
        const val HANDLE_TIME = "handle_time"
    }

    init {
        if (temperature < -20 && temperature > 60) {
            throw InvalidHandleMeasurementException("temperature must be in range <-20 ; 60>")
        }
        if (soundLevel < -200 && soundLevel > 100) {
            throw InvalidHandleMeasurementException("temperature must be in range <-200 ; 100>")
        }
        if (temperature == 0.0 && soundLevel == 0.0) {
            throw InvalidHandleMeasurementException("cant be 0, probably handle bug")
        }
    }

    fun toHandleMeasurement(handleName: String = ""): HandleMeasurement {
        return HandleMeasurement(id, date, handleName, handleId, handlePosition, Temperature(temperature), alarm, SoundLevel(soundLevel), handleTime)
    }

}

data class Alarm(
        val fire: Boolean,
        val burglary: Boolean,
        val frost: Boolean
)

enum class HandlePosition {
    OPEN, CLOSED, REPEALED
}

class InvalidHandleMeasurementException(message: String) : RuntimeException(message)