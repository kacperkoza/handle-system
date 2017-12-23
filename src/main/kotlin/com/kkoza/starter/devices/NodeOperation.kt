package com.kkoza.starter.devices

class NodeOperation(
        private val nodeRepository: NodeRepository
) {

    fun insert(nodeDocument: NodeDocument): NodeDocument {
        validateNodeName(nodeDocument)
        return nodeRepository.insert(nodeDocument)
    }

    fun save(nodeDocument: NodeDocument) {
        validateNodeName(nodeDocument)
        nodeRepository.save(nodeDocument)
    }

    private fun validateNodeName(nodeDocument: NodeDocument) {
        if (nodeDocument.name.isBlank()) {
            throw EmptyNodeNameException()
        }
    }

}

class EmptyNodeNameException : RuntimeException("Node name cannot be empty")