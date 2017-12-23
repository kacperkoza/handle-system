package com.kkoza.starter.devices.api

import com.kkoza.starter.devices.*
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users/devices/nodes")
@Api(value = "Information about user's handles", description = "Add, get, delete, update user's handles")
class NodeEndpoint(
        private val deviceFacade: DeviceFacade,
        private val sessionService: SessionService
) {
    @ApiOperation(value = "Used to add new node")
    @ApiResponses(ApiResponse(code = 201, message = "Successfully added new node. See 'Location' in headers"),
            ApiResponse(code = 401, message = "Expired or invalid cookie session"),
            ApiResponse(code = 422, message = "Handle with given ID already exists or node name is empty"))
    @PostMapping
    fun addNewNode(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @RequestBody(required = true) nodeDto: NodeDto
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val handle = deviceFacade.insertNode(NodeDocument(nodeDto.id, nodeDto.name, userId))
        return ResponseEntity.created(URI("/users/devices/nodes/${handle.id}")).body(null)
    }

    @ApiOperation(value = "Get all user's nodes")
    @ApiResponses(ApiResponse(code = 200, message = "Return list of user's nodes"))
    @GetMapping
    fun getAllNodes(
            @CookieValue("SESSIONID", required = true) sessionId: String
    ): ResponseEntity<NodeList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val list = deviceFacade.findNodeByUserId(userId)
        return ResponseEntity.ok(NodeList(list))
    }

    @ApiOperation(value = "Get node by id")
    @ApiResponses(ApiResponse(code = 200, message = "Returns handleAlarmFilterEx with given id"),
            ApiResponse(code = 404, message = "Requested resource does not exists"))
    @GetMapping("/{nodeId}")
    fun getByNodeId(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("nodeId", required = true) handleId: String
    ): ResponseEntity<NodeDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val node: NodeDocument? = deviceFacade.findNodeById(handleId)
        return if (node != null) {
            ResponseEntity.ok(NodeDto(node.id, node.name))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @ApiOperation(value = "Override node")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully overridden. Nothing to return"),
            ApiResponse(code = 404, message = "Requested resource does not exists"),
            ApiResponse(code = 422, message = "Handle name was empty"))
    @PutMapping("/{nodeId}")
    fun updateNode(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable(name = "nodeId", required = true) nodeId: String,
            @RequestBody(required = true) name: String
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        deviceFacade.saveNode(NodeDocument(nodeId, name, userId))
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation(value = "Delete node by id")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully deleted. Nothing to return"))
    @DeleteMapping("/{nodeId}")
    fun deleteNode(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("nodeId") handleId: String): ResponseEntity<Void> {
        deviceFacade.deleteNodeById(handleId)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(ExistingNodeException::class)
    fun handle(ex: ExistingHandleException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

    @ExceptionHandler(EmptyNodeNameException::class)
    fun handle(ex: EmptyHandleNameException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

}

data class NodeDto(
        val id: String,
        val name: String
)

data class NodeList(
        val nodes: List<NodeDto>
)

