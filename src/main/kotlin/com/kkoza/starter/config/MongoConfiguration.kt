package com.kkoza.starter.config

import com.mongodb.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration


@Configuration
class MongoConfiguration(
        private val properties: MongoProperties,
        @Value("\${application.name}") private val applicationName: String
) : AbstractMongoConfiguration() {

    override fun mongo(): Mongo {
        return MongoClient(
                getServerAddress(),
                getCredentialList(),
                getMongoClientOptions()
        )
    }

    fun getServerAddress(): ServerAddress = ServerAddress()

    private fun getCredentialList(): List<MongoCredential> {
        val credentials = MongoCredential
                .createCredential(properties.username, properties.database, properties.password.toCharArray())
        return listOf(credentials)
    }

    private fun getMongoClientOptions(): MongoClientOptions {
        return MongoClientOptions.builder()
                .applicationName(applicationName)
                .socketTimeout(properties.socketTimeout)
                .build()
    }


    override fun getDatabaseName(): String = properties.database
}


@ConfigurationProperties(value = "mongodb")
data class MongoProperties(
        val host: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String,
        val socketTimeout: Int
)