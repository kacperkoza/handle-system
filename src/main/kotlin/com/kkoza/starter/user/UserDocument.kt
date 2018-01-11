package com.kkoza.starter.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = UserDocument.USERS_COLLECTION)
class UserDocument(

        @Id
        @Field(USER_ID)
        val userId: String? = null,

        @Field(EMAIL)
        @Indexed(unique = true)
        val email: String,

        @Field(PASSWORD)
        val password: String,

        @Field(PHONE_NUMBER)
        val phoneNumber: String

) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val USER_ID = "_id"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val PHONE_NUMBER = "phone_number"
    }

}