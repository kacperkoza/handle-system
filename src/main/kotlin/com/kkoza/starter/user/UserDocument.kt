package com.kkoza.starter.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Field

class UserDocument(

        @Id
        @Field(USER_ID)
        @Indexed(unique = true)
        val userId: String? = null,

        @Field(LOGIN)
        @Indexed(unique = true)
        val login: String,

        @Field(PASSWORD)
        val password: String,

        @Field(NAME)
        val name: String,

        @Field(SURNAME)
        val surname: String,

        @Field(EMAIL)
        @Indexed(unique = true)
        val email: String,

        @Field(PHONE_NUMBER)
        val phoneNumber: String

) {
    companion object {
        const val USER_ID = "_id"
        const val LOGIN = "login"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val EMAIL = "email"
        const val PHONE_NUMBER = "phone_number"
    }

}