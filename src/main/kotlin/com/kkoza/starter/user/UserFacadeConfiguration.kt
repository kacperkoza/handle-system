package com.kkoza.starter.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class UserFacadeConfiguration {

    @Bean
    fun userFacade(mongoTemplate: MongoTemplate): UserFacade {
        val userRepository = UserRepository(mongoTemplate)
        val userOperation = UserOperation(userRepository, UserValidator())
        return UserFacade(userOperation)

    }
}