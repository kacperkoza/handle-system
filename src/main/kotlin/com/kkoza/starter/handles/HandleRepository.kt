package com.kkoza.starter.handles

import org.apache.log4j.Logger
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Repository
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

    fun findById(handleId: String): HandleDto {
        logger.info("Find handle by id = $handleId")
        val handleDocument = mongoTemplate.findOne(Query(Criteria.where(HandleDocument.ID).`is`(handleId)),
                HandleDocument::class.java)
        return HandleDto(handleDocument.id, handleDocument.name)
    }

    fun deleteById(handleId: String) {
        logger.info("Delete handle id = $handleId")
        mongoTemplate.remove(
                Query(Criteria.where(HandleDocument.ID).`is`(handleId)),
                HandleDocument::class.java)
    }

}