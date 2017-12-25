package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.DeviceDto

open class DeviceFacade(
        private val deviceRepository: DeviceRepository,
        private val handleOperation: HandleOperation) {

    fun findHandleByUserId(userId: String): List<DeviceDto> = deviceRepository.findByUserId(userId)

    fun findHandleById(handleId: String): DeviceDocument? = deviceRepository.findById(handleId)

    fun deleteHandleById(handleId: String) = deviceRepository.deleteById(handleId)

    fun insertHandle(deviceDocument: DeviceDocument): DeviceDocument = handleOperation.insert(deviceDocument)

    fun saveHandle(deviceDocument: DeviceDocument) = handleOperation.save(deviceDocument)

}