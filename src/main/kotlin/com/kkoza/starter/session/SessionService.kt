package com.kkoza.starter.session

import com.kkoza.starter.user.UserRepository
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
class SessionService(val userRepository: UserRepository,
                     val mongoTemplate: MongoTemplate) {

    fun createSession(userId: String): String {
        val session = Session(null, userId, DateTime.now())
        mongoTemplate.save(session)
        return session.id!!
    }

    fun findUserId(sessionId: String): String {
        val session = mongoTemplate.findOne(
                Query(Criteria.where(Session.SESSION_ID).`is`(sessionId)),
                Session::class.java) ?: throw InvalidSessionException(sessionId)
        return session.userId!!
    }

    fun updateSession(sessionId: String) {
        mongoTemplate.updateFirst(
                Query(Criteria.where(Session.SESSION_ID).`is`(sessionId)),
                Update().set(Session.VALID, DateTime.now()),
                Session::class.java
        )
    }
}

class InvalidSessionException(sessionId: String) : RuntimeException("Session $sessionId is not valid")

class NotExistingUserException(login: String) : RuntimeException("User with provided credentials login = $login not found")

@Document(collection = Session.SESSIONS)
data class Session(
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