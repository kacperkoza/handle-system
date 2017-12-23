package com.kkoza.starter.devices

import com.kkoza.starter.devices.api.NodeDto
import org.apache.log4j.Logger
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.invoke.MethodHandles

class NodeRepository(
        private val mongoTemplate: MongoTemplate
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun findByUserId(userId: String): List<NodeDto> {
        logger.info("Find nodes for userId = $userId")
        return mongoTemplate.find(
                Query(Criteria.where(NodeDocument.USER_ID).`is`(userId)),
                NodeDocument::class.java).map { NodeDto(it.id, it.name) }
    }

    fun findById(handleId: String): NodeDocument? {
        logger.info("Find node by id = $handleId")
        return mongoTemplate.findOne(Query(Criteria.where(NodeDocument.ID).`is`(handleId)),
                NodeDocument::class.java)
    }

    fun deleteById(nodeId: String) {
        logger.info("Delete node by id = $nodeId")
        mongoTemplate.remove(
                Query(Criteria.where(HandleDocument.ID).`is`(nodeId)),
                NodeDocument::class.java)
    }

    fun insert(nodeDocument: NodeDocument): NodeDocument {
        logger.info("Insert new node = $nodeDocument")
        try {
            mongoTemplate.insert(nodeDocument)
        } catch (ex: DuplicateKeyException) {
            throw ExistingNodeException(nodeDocument.id)
        }
        return nodeDocument
    }

    fun save(nodeDocument: NodeDocument) {
        logger.info("Put new node = $nodeDocument")
        mongoTemplate.save(nodeDocument)
    }
}

class ExistingNodeException(nodeId: String) : RuntimeException("Node with given id = $nodeId already exists")