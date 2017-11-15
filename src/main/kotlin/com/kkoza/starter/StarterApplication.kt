package com.kkoza.starter

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class StarterApplication

fun main(args: Array<String>) {
    SpringApplication.run(StarterApplication::class.java, *args)
}

