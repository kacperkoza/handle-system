package com.kkoza.starter.measurements.dev

import com.kkoza.starter.measurements.*
import org.joda.time.DateTime
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

/**
 * Creates some fake documents and saves in database during app start
 */
@Component
@Profile("!integration")
class DataCreator(
        measurementFacade: MeasurementFacade
) {

    companion object {
        val list = HandlePosition.values()
    }

    init {
        val random = Random()
        measurementFacade.add(Measurement(
                null,
                DateTime.now(),
                HandlePosition.CLOSED,
                Temperature(random.nextDouble() * 30),
                Alarm(false, false, false),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300)
        ))
        measurementFacade.add(Measurement(
                null,
                DateTime.now().minusMinutes(15),
                list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(random.nextBoolean() && random.nextBoolean(), false, false),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300)
        ))
        measurementFacade.add(Measurement(
                null,
                DateTime.now().minusMinutes(30),
                list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(false, random.nextBoolean() && random.nextBoolean(), false),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300)
        ))

        measurementFacade.add(Measurement(
                null,
                DateTime.now().minusMinutes(45),
                list[random.nextInt(3)],
                Temperature(random.nextDouble() * 30),
                Alarm(false, false, random.nextBoolean() && random.nextBoolean()),
                SoundLevel(random.nextDouble() * (-50)),
                random.nextInt(300)
        ))
    }
}