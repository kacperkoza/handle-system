package com.kkoza.starter.settings

import com.kkoza.starter.settings.SettingsDocument.Companion.COLLECTION_NAME
import org.apache.log4j.Logger
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Repository
class SettingsRepository(
        private val mongoTemplate: MongoTemplate
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun save(settingsDocument: SettingsDocument) {
        logger.info("Save settingsDocument $settingsDocument")
        mongoTemplate.save(settingsDocument)
    }

    fun updateTemperature(userId: String, temperature: Double) {
        logger.info("Set new temperature")
        mongoTemplate.updateFirst(
                Query(whereUserIdCriteria(userId)),
                Update.update(SettingsDocument.TEMPERATURE, temperature),
                SettingsDocument::class.java)
    }

    fun enableMotionAlarm(userId: String) {
        logger.info("Enable motion alarm for $userId")
        setMotionAlarm(userId, true)
    }

    fun disableMotionAlarm(userId: String) {
        logger.info("Disable motion alarm for $userId")
        setMotionAlarm(userId, false)
    }

    private fun setMotionAlarm(userId: String, enable: Boolean) {
        mongoTemplate.updateFirst(
                Query(whereUserIdCriteria(userId)),
                updateAlarmEnabled(enable),
                SettingsDocument::class.java)
    }

    private fun updateAlarmEnabled(enable: Boolean) = Update.update(SettingsDocument.ALARM_ENABLED, enable)

    private fun whereUserIdCriteria(userId: String): Criteria = Criteria.where(SettingsDocument.ID).`is`(userId)

    fun findByUserId(userId: String): SettingsDocument {
        logger.info("Get settings for userId = $userId")
        return mongoTemplate.findOne(Query(whereUserIdCriteria(userId)), SettingsDocument::class.java)
    }

}


@Document(collection = COLLECTION_NAME)
data class SettingsDocument(

        @Id
        @Field(ID)
        val userId: String,

        @Field(TEMPERATURE)
        val minTemperature: Double,

        @Field(ALARM_ENABLED)
        val alarmEnabled: Boolean

) {

    companion object {
        const val COLLECTION_NAME = "settings"

        const val TEMPERATURE = "temperature"
        const val ALARM_ENABLED = "alarm_enabled"
        const val ID = "_id"
    }

}