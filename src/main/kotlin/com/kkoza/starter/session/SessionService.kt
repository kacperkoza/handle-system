package com.kkoza.starter.session

import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class SessionService(val mongoTemplate: MongoTemplate) {

    fun createSession(userId: String): String {
        val session = SessionDocument(null, userId, DateTime.now())
        mongoTemplate.save(session)
        return session.id!!
    }

    fun findUserIdAndUpdateSession(sessionId: String): String {
        val session = mongoTemplate.findOne(
                Query(Criteria.where(SessionDocument.SESSION_ID).`is`(sessionId)),
                SessionDocument::class.java) ?: throw InvalidSessionException(sessionId)
        updateSession(sessionId)
        return session.userId
    }

    private fun updateSession(sessionId: String) {
        mongoTemplate.updateFirst(
                Query(Criteria.where(SessionDocument.SESSION_ID).`is`(sessionId)),
                Update().set(SessionDocument.VALID, DateTime.now()),
                SessionDocument::class.java
        )
    }
}

class InvalidSessionException(sessionId: String) : RuntimeException("Session $sessionId is not valid")

class NotExistingUserException(login: String) : RuntimeException("User with provided credentials email = $login not found")

@Document(collection = SessionDocument.SESSIONS)
data class SessionDocument(
        @Id
        @Field(SESSION_ID)
        val id: String?,

        @Field(USER_ID)
        val userId: String,

        @Indexed(unique = true, expireAfterSeconds = ONE_HOUR_IN_SECONDS)
        @Field(VALID)
        val valid: DateTime
) {
    companion object {
        const val SESSIONS = "sessions"
        const val SESSION_ID = "_id"
        const val USER_ID = "userId"
        const val VALID = "valid"
        const val ONE_HOUR_IN_SECONDS = 3600
    }
}