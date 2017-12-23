package com.kkoza.starter.devices.api

import com.kkoza.starter.devices.EmptyHandleNameException
import com.kkoza.starter.devices.ExistingHandleException
import com.kkoza.starter.devices.HandleDocument
import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users/devices/handles")
@Api(value = "Information about user's handles", description = "Add, get, delete, update user's handles")

class HandleEndpoint(
        private val deviceFacade: DeviceFacade,
        private val sessionService: SessionService
) {
    @ApiOperation(value = "Used to add new handleAlarmFilterEx")
    @ApiResponses(ApiResponse(code = 201, message = "Successfully added new handleAlarmFilterEx. See 'Location' in headers"),
            ApiResponse(code = 401, message = "Expired or invalid cookie session"),
            ApiResponse(code = 422, message = "Handle with given ID already exists or handleAlarmFilterEx name is empty"))
    @PostMapping
    fun addNewHandle(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @RequestBody(required = true) handleDto: NodeDto
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val handle = deviceFacade.insertHandle(HandleDocument(handleDto.id, handleDto.name, userId))
        return ResponseEntity.created(URI("/users/devices/handles/${handle.id}")).body(null)
    }

    @ApiOperation(value = "Get all user's handles")
    @ApiResponses(ApiResponse(code = 200, message = "Return list of user's handles"))
    @GetMapping
    fun getAllHandles(
            @CookieValue("SESSIONID", required = true) sessionId: String
    ): ResponseEntity<HandleList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val list = deviceFacade.findHandleByUserId(userId)
        return ResponseEntity.ok(HandleList(list))
    }

    @ApiOperation(value = "Get handleAlarmFilterEx by id")
    @ApiResponses(ApiResponse(code = 200, message = "Returns handleAlarmFilterEx with given id"),
            ApiResponse(code = 404, message = "Requested resource does not exists"))
    @GetMapping("/{handleId}")
    fun getByHandleId(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("handleId", required = true) handleId: String
    ): ResponseEntity<NodeDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val handle: HandleDocument? = deviceFacade.findHandleById(handleId)
        return if (handle != null) {
            ResponseEntity.ok(NodeDto(handle.id, handle.name))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @ApiOperation(value = "Override handleAlarmFilterEx if does not exists")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully overridden. Nothing to return"),
            ApiResponse(code = 404, message = "Requested resource does not exists"),
            ApiResponse(code = 422, message = "Handle name was empty"))
    @PutMapping("/{handleId}")
    fun updateHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable(name = "handleId", required = true) handleId: String,
            @RequestBody(required = true) name: String
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        deviceFacade.saveHandle(HandleDocument(handleId, name, userId))
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation(value = "Override handleAlarmFilterEx if does not exists")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully deleted. Nothing to return"))
    @DeleteMapping("/{handleId}")
    fun deleteHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("handleId") handleId: String): ResponseEntity<Void> {
        deviceFacade.deleteHandleById(handleId)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(ExistingHandleException::class)
    fun handle(ex: ExistingHandleException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

    @ExceptionHandler(EmptyHandleNameException::class)
    fun handle(ex: EmptyHandleNameException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

}

data class HandleDto(
        val id: String,
        val name: String
)

data class HandleList(
        val handles: List<NodeDto>
)

