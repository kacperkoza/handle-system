package com.kkoza.starter.nodes

import com.kkoza.starter.handles.Temperature
import org.joda.time.DateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field


@Document(collection = NodeMeasurementDocument.COLLECTION_NAME)
data class NodeMeasurementDocument(
        @Id
        @Field(ID)
        val id: String? = null,

        @CreatedDate
        @Field(DATE)
        val date: DateTime,

        @Field(NODE_ID)
        val nodeId: String,

        @Field(TEMPERATURE)
        val temperature: Temperature,

        @Field(HUMIDITY)
        val humidity: Humidity,

        @Field(LIGHT_INTENSITY)
        val lightIntensity: LightIntensity,

        @Field(DETECTED_MOTION)
        val detectedMotion: Boolean,

        @Field(CARBON_DIOXIDE)
        val carbonDioxide: CarbonDioxide

) {
    companion object {
        const val COLLECTION_NAME = "nodes_measurements"

        const val ID = "_id"
        const val DATE = "date"
        const val NODE_ID = "handle_id"

        const val TEMPERATURE = "temperature"
        const val HUMIDITY = "humidity"
        const val LIGHT_INTENSITY = "light_intensity"
        const val DETECTED_MOTION = "detected_motion"
        const val CARBON_DIOXIDE = "carbon_DIOXIDE"


    }

    fun toNodeMeasurement(name: String): NodeMeasurement = NodeMeasurement(id, date, name, temperature, lightIntensity, humidity, carbonDioxide)

}

data class Humidity(
        val value: Double,
        val unit: String = "jednostka?"
)

data class LightIntensity(
        val value: Double,
        val unit: String = "Lumeny?"
)

data class CarbonDioxide(
        val value: Double,
        val unit: String = "CO2"
)
