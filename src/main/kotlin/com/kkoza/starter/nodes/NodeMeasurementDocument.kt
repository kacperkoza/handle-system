package com.kkoza.starter.nodes

import com.kkoza.starter.handles.dto.Temperature
import com.kkoza.starter.nodes.dto.CarbonDioxide
import com.kkoza.starter.nodes.dto.Humidity
import com.kkoza.starter.nodes.dto.LightIntensity
import com.kkoza.starter.nodes.dto.NodeMeasurement
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
        val temperature: Double,

        @Field(HUMIDITY)
        val humidity: Double,

        @Field(LIGHT_INTENSITY)
        val lightIntensity: Double,

        @Field(DETECTED_MOTION)
        val detectedMotion: Boolean,

        @Field(CARBON_DIOXIDE)
        val carbonDioxide: Double

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
        const val CARBON_DIOXIDE = "carbon_dioxide"
    }

    init {
        if (temperature < -20 || temperature > 50) {
            throw InvalidNodeMeasurementException("temperature must be in range <20 ; 50>")
        }
        if (humidity < 0 || humidity > 100) {
            throw InvalidNodeMeasurementException("humidity must be in range <0 ; 100>")
        }
        if (lightIntensity < 0 || lightIntensity > 100000) {
            throw InvalidNodeMeasurementException("light intensity must be in range <0 ; 100000>")
        }
        if (carbonDioxide < 0 || carbonDioxide > 100) {
            throw InvalidNodeMeasurementException("carbonDioxide must be in range <0 ; 100>")
        }
    }

    fun toNodeMeasurement(nodeName: String): NodeMeasurement = NodeMeasurement(
            id,
            date,
            nodeName,
            nodeId,
            Temperature(temperature),
            LightIntensity(lightIntensity),
            Humidity(humidity),
            detectedMotion,
            CarbonDioxide(carbonDioxide))

}

class InvalidNodeMeasurementException(message: String) : RuntimeException(message)

