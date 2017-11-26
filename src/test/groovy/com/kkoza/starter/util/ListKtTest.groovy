package com.kkoza.starter.util

import spock.lang.Specification

import static com.kkoza.starter.util.ListKt.dropIfNotNull
import static com.kkoza.starter.util.ListKt.takeIfNotNull

class ListKtTest extends Specification {

    def "should drop first #n elements when n is not null"() {
        given:
        def list = [1, 2, 3, 4]

        when:
        list = dropIfNotNull(list, n)

        then:
        list == expectedList

        where:
        n || expectedList
        2 || [3, 4]
        3 || [4]
        5 || []
    }

    def "should not drop when n is null"() {
        given:
        def list = [1, 2, 3, 4]
        def n = null

        when:
        list = dropIfNotNull(list, n)

        then:
        list == [1, 2, 3, 4]
    }

    def "should take first #n elements when n is not null"() {
        given:
        def list = [1, 2, 3, 4]

        when:
        list = takeIfNotNull(list, offset)

        then:
        list == expectedList

        where:
        offset || expectedList
        2      || [1, 2]
        3      || [1, 2, 3]
        5      || [1, 2, 3, 4]
    }

    def "should not take when n is null"() {
        given:
        def list = [1, 2, 3, 4]
        def n = null

        when:
        list = takeIfNotNull(list, n)

        then:
        list == [1, 2, 3, 4]
    }

}
