package com.kkoza.starter.information

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext

@RestController
@RequestMapping(value = "/status")
class InformationEndpoint(private val webApplicationContext: WebApplicationContext) {

    @GetMapping(value = "/beans")
    fun getAllBeans(): ResponseEntity<Array<String>> {
        val allBeans = webApplicationContext.beanDefinitionNames
        return ResponseEntity(allBeans, HttpStatus.OK)
    }

}