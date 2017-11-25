package com.kkoza.starter.util

import com.kkoza.starter.measurements.Alarm
import com.kkoza.starter.measurements.HandlePosition
import com.kkoza.starter.measurements.Measurement
import com.kkoza.starter.measurements.SoundLevel
import com.kkoza.starter.measurements.Temperature
import org.joda.time.DateTime

class MeasurementBuilder {

    String id = 'any'
    DateTime date = DateTime.now()
    String handleId = 'handle-id'
    HandlePosition handlePosition = HandlePosition.CLOSED
    Double temperature = 20.3d
    boolean fire = false
    boolean burglar = false
    boolean frost = false
    Double sound = -35.3d
    Integer handleTime = 110

    static MeasurementBuilder create() {
        return new MeasurementBuilder()
    }

    MeasurementBuilder setId(String id) {
        this.id = id
        return this
    }

    MeasurementBuilder setDate(DateTime date) {
        this.date = date
        return this
    }

    MeasurementBuilder setHandleId(String handleId) {
        this.handleId = handleId
        return this
    }

    MeasurementBuilder setHandlePosition(HandlePosition handlePosition) {
        this.handlePosition = handlePosition
        return this
    }

    MeasurementBuilder setTemperature(Double temperature) {
        this.temperature = temperature
        return this
    }

    MeasurementBuilder setAlarm(boolean fire, boolean burglar, boolean frost) {
        this.fire = fire
        this.burglar = burglar
        this.frost = frost
        return this
    }

    MeasurementBuilder setSound(Double sound) {
        this.sound = sound
        return this
    }

    MeasurementBuilder setHandleTime(Integer handleTime) {
        this.handleTime
        return this
    }

    Measurement build() {
        return new Measurement(
                id,
                date,
                handleId,
                handlePosition,
                new Temperature(temperature, "°C"),
                new Alarm(fire, burglar, frost),
                new SoundLevel(sound, "dB"),
                handleTime
        )
    }

}
