package com.kkoza.starter.measurements.dev

import com.kkoza.starter.measurements.*
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
@Profile("local")
class ScheduledAdder(val measurementFacade: MeasurementFacade) {

    private val random = Random()

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    @Scheduled(fixedDelay = 3000)
    fun add() {
        logger.info("Scheduler added new measurement")
        val measurement = Measurement(
                null,
                DateTime.now().minusMinutes(30),
                "handle-id",
                DataCreator.list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean()),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300))
        measurementFacade.add(measurement)
    }

}

@Configuration
@EnableScheduling
class Config