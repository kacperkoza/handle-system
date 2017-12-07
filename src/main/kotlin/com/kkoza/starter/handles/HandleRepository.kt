package com.kkoza.starter.handles

import com.kkoza.starter.handles.api.HandleDto
import org.apache.log4j.Logger
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.invoke.MethodHandles

class HandleRepository(private val mongoTemplate: MongoTemplate) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun findByUserId(userId: String): List<HandleDto> {
        logger.info("Find handles for userId = $userId")
        return mongoTemplate.find(
                Query(Criteria.where(HandleDocument.USER_ID).`is`(userId)),
                HandleDocument::class.java).map { HandleDto(it.id, it.name) }
    }

    fun findById(handleId: String): HandleDocument? {
        logger.info("Find handle by id = $handleId")
        return mongoTemplate.findOne(Query(Criteria.where(HandleDocument.ID).`is`(handleId)),
                HandleDocument::class.java)
    }

    fun deleteById(handleId: String) {
        logger.info("Delete handle id = $handleId")
        mongoTemplate.remove(
                Query(Criteria.where(HandleDocument.ID).`is`(handleId)),
                HandleDocument::class.java)
    }

    fun insert(handleDocument: HandleDocument): HandleDocument {
        logger.info("Insert new handle $handleDocument")
        try {
            mongoTemplate.insert(handleDocument)
        } catch (ex: DuplicateKeyException) {
            throw ExistingHandleException(handleDocument.id)
        }
        return handleDocument
    }

    fun save(handleDocument: HandleDocument) {
        logger.info("Put new handle $handleDocument")
        mongoTemplate.save(handleDocument)
    }

}