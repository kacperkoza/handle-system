package com.kkoza.starter.util

import org.joda.time.DateTime
import spock.lang.Specification

import static com.kkoza.starter.util.DateKt.dayFormat
import static com.kkoza.starter.util.DateKt.hourFormat

class DateKtTest extends Specification {

    def "should format date to 'dd-MM-yyy'"() {
        given:
        DateTime date = new DateTime(2010,10,24,10,10)

        when:
        def formattedDate = dayFormat(date)

        then:
        formattedDate == '24-10-2010'
    }

    def "should format hour to 'mm:HH'"() {
        given:
        DateTime date = new DateTime(2010,10,24,10,20)

        when:
        def formattedDate = hourFormat(date)

        then:
        formattedDate == '10:20'
    }
}
