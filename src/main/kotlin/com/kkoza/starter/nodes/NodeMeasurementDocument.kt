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


