package com.kkoza.starter.measurements

import com.github.fakemongo.Fongo
import com.kkoza.starter.infrastructure.smsclient.SmsClient
import com.kkoza.starter.user.UserRepository
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

class MeasurementFacadeSpec extends Specification {


    MongoTemplate mongoTemplate = new MongoTemplate(new Fongo("testdb").getMongo(), "test")
    MeasurementRepository measurementRepository = new MeasurementRepository(mongoTemplate)

    SmsClient smsClient = Mock(SmsClient)
    DangerEventNotifier dangerEventNotifier = new DangerEventNotifier(smsClient)

    UserRepository userRepository = Stub(UserRepository)

    MeasurementFacade measurementFacade

    def setup() {
        measurementFacade = new MeasurementFacade(
                new MeasurementOperation(measurementRepository, dangerEventNotifier, userRepository)
        )
    }


}
