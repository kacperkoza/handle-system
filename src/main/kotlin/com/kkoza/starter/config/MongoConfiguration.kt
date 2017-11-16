package com.kkoza.starter.config

import com.mongodb.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.stereotype.Component


@Configuration
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
                .socketTimeout(mongoProperties.socketTimeout)
                .build()
    }

    override fun getDatabaseName(): String = mongoProperties.database
}

@Component
@EnableConfigurationProperties
@ConfigurationProperties("mongodb")
data class MongoProperties(
        val host: String = "ds261745.mlab.com",
        val port: Int = 61745,
        val database: String = "handlesystem",
        val username: String = "kacper",
        val password: String = "kacper",
        val socketTimeout: Int = 2000
)
