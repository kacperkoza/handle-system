package com.kkoza.starter.testutil

import com.kkoza.starter.nodes.NodeMeasurementDocument
import org.joda.time.DateTime

class NodeBuilder {

    String id = 'any'
    DateTime date = DateTime.now()
    String nodeId = 'node-id'
    Double temperature = 20.3d
    Double humidity = 20.3d
    Double lightIntensity = 10.0
    Boolean motion = false
    Double carbonDioxity = 20.5d

    static NodeBuilder create() {
        return new NodeBuilder()
    }

    NodeBuilder setId(String id) {
        this.id = id
        return this
    }

    NodeBuilder setDate(DateTime date) {
        this.date = date
        return this
    }

    NodeBuilder setNodeId(String nodeId) {
        this.nodeId = nodeId
        return this
    }

    NodeBuilder setTemperature(Double temperature) {
        this.temperature = temperature
        return this
    }

    NodeBuilder setHumidity(Double humidity) {
        this.humidity = humidity
        return this
    }

    NodeBuilder setLightIntensity(Double lightIntensity) {
        this.lightIntensity = lightIntensity
        return this
    }

    NodeBuilder setMotion(Boolean motion) {
        this.motion = motion
        return this
    }

    NodeBuilder setCarbonDioxity(Double carbonDioxity) {
        this.carbonDioxity = carbonDioxity
        return this
    }

    NodeMeasurementDocument build() {
        return new NodeMeasurementDocument(
                id,
                date,
                nodeId,
                temperature,
                humidity,
                lightIntensity,
                motion,
                carbonDioxity
        )
    }

}
