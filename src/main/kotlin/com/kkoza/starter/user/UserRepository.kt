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
        logger.info("Save or update new user ${userDocument.email}")
        mongoTemplate.save(userDocument)
        return userDocument
    }

    fun emailExists(email: String): Boolean {
        val user = mongoTemplate.findOne(
                Query(Criteria(UserDocument.EMAIL).`is`(email)),
                UserDocument::class.java
        )
        return user != null
    }

    fun findByUserId(userId: String): UserDocument? {
        return mongoTemplate.findOne(
                Query(Criteria(UserDocument.USER_ID).`is`(userId)),
                UserDocument::class.java
        )
    }

    fun findUserWithHandle(handleId: String): UserDocument? {
        logger.info("Find user with handle $handleId")
        return mongoTemplate.findOne(
                Query(Criteria.where(UserDocument.HANDLE_IDS).`is`(handleId)),
                UserDocument::class.java
        )
    }

    fun findUserByCredentials(login: String, password: String): UserDocument? {
        logger.info("Find user with credentials login = $login, password = $password")
        return mongoTemplate.findOne(
                Query(Criteria.where(UserDocument.LOGIN).`is`(login).and(UserDocument.PASSWORD).`is`(password)),
                UserDocument::class.java
        )
    }
}
