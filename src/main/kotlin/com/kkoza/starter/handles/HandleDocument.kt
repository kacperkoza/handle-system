package com.kkoza.starter.handles

import com.kkoza.starter.handles.api.HandleDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = HandleDocument.HANDLES_COLLECTION)
data class HandleDocument(

        @Id
        @Field(ID)
        val id: String,

        @Field(HANDLE_NAME)
        val name: String,

        @Field(USER_ID)
        val userId: String

) {
    companion object {
        const val HANDLES_COLLECTION = "handles"
        const val USER_ID = "user_id"
        const val HANDLE_NAME = "handle_name"
        const val ID = "_id"
    }

    fun toDto(): HandleDto = HandleDto(id, name)
}