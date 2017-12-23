package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.NodeDto

open class DeviceFacade(
        private val handleRepository: HandleRepository,
        private val handleOperation: HandleOperation,
        private val nodeOperation: NodeOperation,
        private val nodeRepository: NodeRepository) {

    fun findHandleByUserId(userId: String): List<NodeDto> = handleRepository.findByUserId(userId)

    fun findHandleById(handleId: String): HandleDocument? = handleRepository.findById(handleId)

    fun deleteHandleById(handleId: String) = handleRepository.deleteById(handleId)

    fun insertHandle(handleDocument: HandleDocument): HandleDocument = handleOperation.insert(handleDocument)

    fun saveHandle(handleDocument: HandleDocument) = handleOperation.save(handleDocument)

    fun insertNode(nodeDocument: NodeDocument): NodeDocument = nodeOperation.insert(nodeDocument)

    fun findNodeByUserId(userId: String): List<NodeDto> = nodeRepository.findByUserId(userId)

    fun findNodeById(nodeId: String): NodeDocument? = nodeRepository.findById(nodeId)

    fun saveNode(nodeDocument: NodeDocument) = nodeOperation.save(nodeDocument)

    fun deleteNodeById(nodeId: String) = nodeRepository.deleteById(nodeId)

}