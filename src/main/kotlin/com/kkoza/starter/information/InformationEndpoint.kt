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

    @GetMapping(value = "/hello")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity("Hello world!", HttpStatus.OK)
    }

    @GetMapping(value = "/example")
    fun example(): ResponseEntity<List<Person>> {
        return ResponseEntity(getPersonList(), HttpStatus.OK)
    }

    private fun getPersonList(): List<Person> {
        return listOf(
                Person("John", "Smith"),
                Person("Jacob", "Pelly"),
                Person("Michael", "Fellow"))
    }

}

data class Person(
        val name: String,
        val surname: String
)