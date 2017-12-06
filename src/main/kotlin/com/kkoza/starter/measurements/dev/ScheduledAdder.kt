package com.kkoza.starter.measurements.dev

import com.kkoza.starter.handles.HandleDocument
import com.kkoza.starter.handles.HandleFacade
import com.kkoza.starter.measurements.*
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import org.apache.log4j.Logger
import org.joda.time.DateTime
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles
import java.util.*

@Component
@Profile("local", "prod")
class ScheduledAdder(
        val measurementFacade: MeasurementFacade,
        userFacade: UserFacade,
        handleFacade: HandleFacade
) {

    init {
        if (userFacade.findUserById("kacper") == null) {
            logger.info("Add all users init")
            userFacade.save(UserDocument("kacper", "kacper@gmail.com", "kacper", "123456789"))
            userFacade.save(UserDocument("jeremi", "jeremi@gmail.com", "jeremi1", "123456789"))
            userFacade.save(UserDocument("kamil", "kamil@gmail.com", "kamil1", "123456789"))
        }

        if (handleFacade.findById("klamka1") == null) {
            logger.info("Add all handles init")
            handleFacade.save(HandleDocument("klamka1", "pokoj", "kacper"))
            handleFacade.save(HandleDocument("klamka2", "pokoj", "jeremi"))
            handleFacade.save(HandleDocument("klamka3", "pokoj", "kamil"))
        }
    }

    private val random = Random()

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    @Scheduled(fixedDelay = 5000)
    fun add() {
        val measurement = MeasurementDocument(
                null,
                DateTime.now(),
                "klamka1",
                DataCreator.list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean()),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300))
        val measurement2 = MeasurementDocument(
                null,
                DateTime.now(),
                "klamka2",
                DataCreator.list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean()),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300))
        val measurement3 = MeasurementDocument(
                null,
                DateTime.now(),
                "klamka3",
                DataCreator.list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean()),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300))
        logger.info("Scheduler added new measurement $measurement")
        logger.info("Scheduler added new measurement $measurement2")
        logger.info("Scheduler added new measurement $measurement3")
        measurementFacade.add(measurement)
        measurementFacade.add(measurement2)
        measurementFacade.add(measurement3)
    }

}

@Configuration
@EnableScheduling
class Config