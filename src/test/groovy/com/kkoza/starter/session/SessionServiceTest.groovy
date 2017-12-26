package com.kkoza.starter.session

import com.github.fakemongo.Fongo
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Specification

class SessionServiceTest extends Specification {

    MongoTemplate mongoTemplate = new MongoTemplate(new Fongo('test-db').getMongo(), 'test')

    SessionService sessionService

    def setup() {
        sessionService = new SessionService(mongoTemplate)
    }

    def 'should return session id after creating new session'() {
        when:
        def sessionId = sessionService.createSession('user-id')

        then:
        def createdSession = mongoTemplate.findOne(new Query(), SessionDocument.class)
        sessionId == createdSession.id
    }

    def 'should create session associated with user id'() {
        when:
        sessionService.createSession('user-id')

        then:
        def createdSession = mongoTemplate.findOne(new Query(), SessionDocument.class)
        createdSession.userId == 'user-id'
    }

    def 'should create session with DateTime now'() {
        given:
        def nowMinusMinute = DateTime.now().minusMinutes(1)
        def nowPlusMinute = DateTime.now().plusMinutes(1)

        when:
        sessionService.createSession('user-id')
        def createdSession = mongoTemplate.findOne(new Query(), SessionDocument.class)

        then:
        createdSession.valid.isBefore(nowPlusMinute)
        createdSession.valid.isAfter(nowMinusMinute)
    }

    def 'should remove existing session'() {
        mongoTemplate.save(new SessionDocument('id', 'user-id', DateTime.now()))


        when:
        sessionService.destroySession('id')

        then:
        mongoTemplate.findAll(SessionDocument).size() == 0
    }

}
