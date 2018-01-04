package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.DeviceDto

open class DeviceFacade(
        private val deviceRepository: DeviceRepository,
        private val handleOperation: HandleOperation) {

    fun findByUserId(userId: String): List<DeviceDto> = deviceRepository.findAllByUserId(userId)

    fun findById(deviceId: String): DeviceDocument? = deviceRepository.findById(deviceId)

    fun deleteById(deviceId: String) = deviceRepository.deleteById(deviceId)

    fun insert(deviceDocument: DeviceDocument): DeviceDocument = handleOperation.insert(deviceDocument)

    fun save(deviceDocument: DeviceDocument) = handleOperation.save(deviceDocument)

}