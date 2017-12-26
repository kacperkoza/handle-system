package com.kkoza.starter.user

import com.kkoza.starter.settings.SettingsRepository
import com.kkoza.starter.settings.SettingsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class UserFacadeConfiguration {

    @Bean
    fun userFacade(mongoTemplate: MongoTemplate): UserFacade {
        val userRepository = UserRepository(mongoTemplate)
        val userOperation = UserOperation(userRepository, UserValidator(), SettingsService(SettingsRepository(mongoTemplate)))
        return UserFacade(userOperation, userRepository)

    }
}