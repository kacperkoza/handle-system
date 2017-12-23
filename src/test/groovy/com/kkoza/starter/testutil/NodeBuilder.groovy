package com.kkoza.starter.testutil

import com.kkoza.starter.devices.HandleDocument
import com.kkoza.starter.devices.api.NodeDto

class NodeBuilder {

    String nodeId = 'node-id'
    String nodeName = 'node-name'
    String userId = 'userId'

    static HandleBuilder create() {
        return new HandleBuilder()
    }

    HandleBuilder setNodeId(String handleId) {
        this.nodeId = handleId
        return this
    }

    HandleBuilder setNodeName(String handleName) {
        this.nodeName = handleName
        return this
    }

    HandleBuilder setUserId(String userId) {
        this.userId = userId
        return this
    }

    NodeDto buildDto() {
        return new NodeDto(nodeId, nodeName)
    }

    HandleDocument buildDocument() {
        return new HandleDocument(nodeId, nodeName, userId)
    }

}
