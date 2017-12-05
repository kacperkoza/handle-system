package com.kkoza.starter.testutil

import com.kkoza.starter.handles.HandleDocument
import com.kkoza.starter.handles.HandleDto

class HandleBuilder {

    String handleId = 'handle-id'
    String handleName = 'handle-name'
    String userId = 'userId'

    static HandleBuilder create() {
        return new HandleBuilder()
    }

    HandleBuilder setHandleId(String handleId) {
        this.handleId = handleId
        return this
    }

    HandleBuilder setHandleName(String handleName) {
        this.handleName = handleName
        return this
    }

    HandleBuilder setUserId(String userId) {
        this.userId = userId
        return this
    }

    HandleDto buildDto() {
        return new HandleDto(handleId, handleName)
    }

    HandleDocument buildDocument() {
        return new HandleDocument(handleId, handleName, userId)
    }

}
