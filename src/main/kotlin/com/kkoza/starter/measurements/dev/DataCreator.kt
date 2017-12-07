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
@Profile("local")
class DataCreator(
        measurementFacade: MeasurementFacade
) {



}