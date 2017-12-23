package com.kkoza.starter.devices

import com.kkoza.starter.devices.NodeDocument.Companion.NODES_COLLECTION
import com.kkoza.starter.devices.api.NodeDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = NODES_COLLECTION)
data class NodeDocument(

        @Id
        @Field(ID)
        val id: String,

        @Field(HANDLE_NAME)
        val name: String,

        @Field(USER_ID)
        val userId: String

) {
    companion object {
        const val NODES_COLLECTION = "nodes"
        const val USER_ID = "user_id"
        const val HANDLE_NAME = "node_name"
        const val ID = "_id"
    }

    fun toDto(): NodeDto = NodeDto(id, name)
}