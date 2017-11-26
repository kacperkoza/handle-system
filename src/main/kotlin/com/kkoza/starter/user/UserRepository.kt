package com.kkoza.starter.user

import org.apache.log4j.Logger
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Repository
class UserRepository(val mongoTemplate: MongoTemplate) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun save(userDocument: UserDocument): UserDocument {
        logger.info("Save or update new user ${userDocument.userId}")
        mongoTemplate.save(userDocument)
        return userDocument
    }

    fun loginExists(login: String): Boolean {
        val user = mongoTemplate.findOne(
                Query(Criteria(UserDocument.LOGIN).`is`(login)),
                UserDocument::class.java
        )
        return user != null
    }

    fun emailExists(email: String): Boolean {
        val user = mongoTemplate.findOne(
                Query(Criteria(UserDocument.EMAIL).`is`(email)),
                UserDocument::class.java
        )
        return user != null
    }
}