package com.kkoza.starter.information

import com.kkoza.starter.BaseIntegrationTest

class InformationIntegrationTest extends BaseIntegrationTest {

    def "should return bean 'informationEndpoint' in body"() {
        when:
        def response = restTemplate.getForEntity(
                localUrl("status/beans"),
                String)

        then:
        response.body.contains("informationEndpoint")
    }
}
