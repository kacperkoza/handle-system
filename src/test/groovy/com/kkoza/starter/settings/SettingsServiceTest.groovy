package com.kkoza.starter.settings

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Specification

class SettingsServiceTest extends Specification {

    Fongo fongo = new Fongo('test-db')
    Mongo mongo = fongo.getMongo()
    MongoTemplate mongoTemplate

    SettingsService settingsService

    def setup() {
        mongoTemplate = new MongoTemplate(mongo, 'test-db')
        settingsService = new SettingsService(new SettingsRepository(mongoTemplate))
    }

    def 'should create new settings with default values'() {
        when:
        settingsService.createDefaultSettings('user-id', 10.0d, false)
        SettingsDocument settingsDocument = findOne()

        then:
        with(settingsDocument) {
            userId == 'user-id'
            minTemperature == 10.0d
            alarmEnabled == false
        }
    }

    def 'should disable or enable motion alarm for user'() {
        given:
        save(new SettingsDocument('user-id', 10.0d, true))

        when:
        settingsService.setAlarm('user-id', newAlarmValue)
        SettingsDocument settingsDocument = findOne()

        then:
        settingsDocument.alarmEnabled == newAlarmValue

        where:
        newAlarmValue << [true, false]
    }

    def 'should set new temperature for user'() {
        given:
        save(new SettingsDocument('user-id', 10.0d, true))

        when:
        settingsService.setTemperature('user-id', 25.99d)
        SettingsDocument settingsDocument = findOne()

        then:
        settingsDocument.minTemperature == 25.99d
    }


    private save(SettingsDocument settingsDocument) {
        mongoTemplate.save(settingsDocument)
    }

    private findOne() {
        mongoTemplate.findOne(new Query(), SettingsDocument.class)
    }

}
