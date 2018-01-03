package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.DeviceDto

open class DeviceFacade(
        private val deviceRepository: DeviceRepository,
        private val handleOperation: HandleOperation) {

    fun findByUserId(userId: String): List<DeviceDto> = deviceRepository.findByUserId(userId)

    fun findById(handleId: String): DeviceDocument? = deviceRepository.findById(handleId)

    fun deleteByHandleId(handleId: String) = deviceRepository.deleteById(handleId)

    fun insert(deviceDocument: DeviceDocument): DeviceDocument = handleOperation.insert(deviceDocument)

    fun save(deviceDocument: DeviceDocument) = handleOperation.save(deviceDocument)

}