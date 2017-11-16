package com.kkoza.starter.measurements

import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = Measurement.COLLECTION_NAME)
data class Measurement(
        @Id
        val id: String? = null,

        @Field(DATE)
        val date: DateTime,

        @Field(HANDLE_POSITION)
        val handlePosition: HandlePosition,

        @Field(TEMPERATURE)
        val temperature: Temperature,

        @Field(ALARM)
        val alarm: Alarm,

        @Field(SOUND_LEVEL)
        val soundLevel: SoundLevel,

        @Field(HANDLE_TIME)
        val handleTime: Int //wtf?
) {
    companion object {
        const val COLLECTION_NAME = "measurements"

        const val DATE = "date"
        const val HANDLE_POSITION = "handle_position"
        const val TEMPERATURE = "temperature"
        const val ALARM = "alarm"
        const val SOUND_LEVEL = "sound_level"
        const val HANDLE_TIME = "handle_time"
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
        val unit: String = "°C"
)

enum class HandlePosition {
    OPEN, CLOSED, REPEALED
}