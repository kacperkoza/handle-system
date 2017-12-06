package com.kkoza.starter.config

import com.mongodb.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.stereotype.Component


@Configuration
@Profile("!integration")
class MongoConfiguration(
        private val mongoProperties: MongoProperties,
        @Value("\${application.name}") private val applicationName: String
) : AbstractMongoConfiguration() {

    override fun mongo(): Mongo {
        return MongoClient(
                getServerAddress(),
                getCredentialList(),
                getMongoClientOptions()
        )
    }

    private fun getServerAddress(): ServerAddress = ServerAddress(mongoProperties.host, mongoProperties.port)

    private fun getCredentialList(): List<MongoCredential> {
        val credentials = MongoCredential
                .createCredential(mongoProperties.username, mongoProperties.database, mongoProperties.password.toCharArray())
        return listOf(credentials)
    }

    private fun getMongoClientOptions(): MongoClientOptions {
        return MongoClientOptions.builder()
                .applicationName(applicationName)
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .socketTimeout(mongoProperties.socketTimeout)
                .build()
    }

    override fun getDatabaseName(): String = mongoProperties.database
}

@Component
data class MongoProperties(
        @Value("\${mongodb.host}") var host: String,
        @Value("\${mongodb.port}") val port: Int,
        @Value("\${mongodb.database}") val database: String,
        @Value("\${mongodb.username}") val username: String,
        @Value("\${mongodb.password}") val password: String,
        @Value("\${mongodb.socketTimeout}") val socketTimeout: Int
)
