package com.kkoza.starter.testutil

import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceType
import com.kkoza.starter.devices.api.DeviceDto

class DeviceBuilder {

    String id = 'device-id'
    String deviceName = 'device-name'
    String userId = 'userId'
    DeviceType deviceType = DeviceType.HANDLE

    static DeviceBuilder create() {
        return new DeviceBuilder()
    }

    DeviceBuilder setId(String id) {
        this.id = id
        return this
    }

    DeviceBuilder setDeviceName(String deviceName) {
        this.deviceName = deviceName
        return this
    }

    DeviceBuilder setUserId(String userId) {
        this.userId = userId
        return this
    }

    DeviceBuilder setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType
        return this
    }

    DeviceDto buildDto() {
        return new DeviceDto(id, deviceName, deviceType)
    }

    DeviceDocument buildDocument() {
        return new DeviceDocument(id, deviceName, userId, deviceType)
    }

}
