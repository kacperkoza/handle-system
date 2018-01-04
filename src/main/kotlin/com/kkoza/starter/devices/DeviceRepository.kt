package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.DeviceDto
import org.apache.log4j.Logger
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.invoke.MethodHandles

class DeviceRepository(private val mongoTemplate: MongoTemplate) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun findAllByUserId(userId: String): List<DeviceDto> {
        logger.info("Find all devices for userId = $userId")
        return mongoTemplate.find(
                Query(Criteria.where(DeviceDocument.USER_ID).`is`(userId)),
                DeviceDocument::class.java).map { DeviceDto(it.id, it.name, it.deviceType) }
    }

    fun findById(deviceId: String): DeviceDocument? {
        logger.info("Find device by id = $deviceId")
        return mongoTemplate.findOne(
                Query(Criteria.where(DeviceDocument.ID).`is`(deviceId)),
                DeviceDocument::class.java)
    }

    fun deleteById(handleId: String) {
        logger.info("Delete device with id = $handleId")
        mongoTemplate.remove(
                Query(Criteria.where(DeviceDocument.ID).`is`(handleId)),
                DeviceDocument::class.java)
    }

    fun insert(deviceDocument: DeviceDocument): DeviceDocument {
        logger.info("Insert new device = $deviceDocument")
        try {
            mongoTemplate.insert(deviceDocument)
        } catch (ex: DuplicateKeyException) {
            throw ExistingHandleException(deviceDocument.id)
        }
        return deviceDocument
    }

    fun save(deviceDocument: DeviceDocument) {
        logger.info("Put new device = $deviceDocument")
        mongoTemplate.save(deviceDocument)
    }

}