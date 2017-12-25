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

    fun findByUserId(userId: String): List<DeviceDto> {
        logger.info("Find devices for userId = $userId")
        return mongoTemplate.find(
                Query(Criteria.where(DeviceDocument.USER_ID).`is`(userId)),
                DeviceDocument::class.java).map { DeviceDto(it.id, it.name, it.deviceType) }
    }

    fun findById(handleId: String): DeviceDocument? {
        logger.info("Find handleAlarmFilterEx by id = $handleId")
        return mongoTemplate.findOne(Query(Criteria.where(DeviceDocument.ID).`is`(handleId)),
                DeviceDocument::class.java)
    }

    fun deleteById(handleId: String) {
        logger.info("Delete handleAlarmFilterEx id = $handleId")
        mongoTemplate.remove(
                Query(Criteria.where(DeviceDocument.ID).`is`(handleId)),
                DeviceDocument::class.java)
    }

    fun insert(deviceDocument: DeviceDocument): DeviceDocument {
        logger.info("Insert new handleAlarmFilterEx $deviceDocument")
        try {
            mongoTemplate.insert(deviceDocument)
        } catch (ex: DuplicateKeyException) {
            throw ExistingHandleException(deviceDocument.id)
        }
        return deviceDocument
    }

    fun save(deviceDocument: DeviceDocument) {
        logger.info("Put new handleAlarmFilterEx $deviceDocument")
        mongoTemplate.save(deviceDocument)
    }

}