package com.kkoza.starter.testutil

import com.kkoza.starter.handles.Alarm
import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.handles.HandlePosition
import org.joda.time.DateTime

class HandleBuilder {

    String id = 'any'
    DateTime date = DateTime.now()
    String handleId = 'handleAlarmFilterEx-id'
    HandlePosition handlePosition = HandlePosition.CLOSED
    Double temperature = 20.3d
    boolean fire = false
    boolean burglar = false
    boolean frost = false
    Double sound = -35.3d
    Integer handleTime = 110

    static HandleBuilder create() {
        return new HandleBuilder()
    }

    HandleBuilder setId(String id) {
        this.id = id
        return this
    }

    HandleBuilder setDate(DateTime date) {
        this.date = date
        return this
    }

    HandleBuilder setHandleId(String handleId) {
        this.handleId = handleId
        return this
    }

    HandleBuilder setHandlePosition(HandlePosition handlePosition) {
        this.handlePosition = handlePosition
        return this
    }

    HandleBuilder setTemperature(Double temperature) {
        this.temperature = temperature
        return this
    }

    HandleBuilder setAlarm(boolean fire, boolean burglar, boolean frost) {
        this.fire = fire
        this.burglar = burglar
        this.frost = frost
        return this
    }

    HandleBuilder setSound(Double sound) {
        this.sound = sound
        return this
    }

    HandleBuilder setHandleTime(Integer handleTime) {
        this.handleTime
        return this
    }

    HandleMeasurementDocument build() {
        return new HandleMeasurementDocument(
                id,
                date,
                handleId,
                handlePosition,
                temperature,
                new Alarm(fire, burglar, frost),
                sound,
                handleTime
        )
    }

}
