package com.kkoza.starter.testutil

import com.kkoza.starter.devices.HandleDocument
import com.kkoza.starter.devices.api.NodeDto

class HandleBuilder {

    String handleId = 'handleAlarmFilterEx-id'
    String handleName = 'handleAlarmFilterEx-name'
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

    NodeDto buildDto() {
        return new NodeDto(handleId, handleName)
    }

    HandleDocument buildDocument() {
        return new HandleDocument(handleId, handleName, userId)
    }

}
