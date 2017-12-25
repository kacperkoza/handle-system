package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.DeviceDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = DeviceDocument.DEVICES_COLLECTION)
data class DeviceDocument(

        @Id
        @Field(ID)
        val id: String,

        @Field(HANDLE_NAME)
        val name: String,

        @Field(USER_ID)
        val userId: String,

        @Field(DEVICE_TYPE)
        val deviceType: DeviceType

) {
    companion object {
        const val DEVICES_COLLECTION = "devices"
        const val ID = "_id"
        const val USER_ID = "user_id"
        const val HANDLE_NAME = "handle_name"
        const val DEVICE_TYPE = "device_type"
    }

    fun toDto(): DeviceDto = DeviceDto(id, name, deviceType)
}

enum class DeviceType {
    HANDLE, NODE
}