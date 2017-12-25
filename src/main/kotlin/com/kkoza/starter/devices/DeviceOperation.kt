package com.kkoza.starter.devices


class HandleOperation(private val deviceRepository: DeviceRepository) {

    fun insert(deviceDocument: DeviceDocument): DeviceDocument {
        validateHandleName(deviceDocument)
        return deviceRepository.insert(deviceDocument)
    }

    fun save(deviceDocument: DeviceDocument) {
        validateHandleName(deviceDocument)
        deviceRepository.save(deviceDocument)
    }

    private fun validateHandleName(deviceDocument: DeviceDocument) {
        if (deviceDocument.name.isBlank()) {
            throw EmptyHandleNameException()
        }
    }

}

class ExistingHandleException(id: String) : RuntimeException("Handle with $id already exists")

class EmptyHandleNameException : RuntimeException("Handle name can't be blank")
