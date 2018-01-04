package com.kkoza.starter.devices.api

import com.kkoza.starter.devices.*
import com.kkoza.starter.session.SessionService
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users/devices")
@Api(value = "Information about user's devices", description = "Add, get, delete, update user's devices")

class DeviceEndpoint(
        private val deviceFacade: DeviceFacade,
        private val sessionService: SessionService
) {
    @ApiOperation(value = "Used to add new device (NODE or HANDLE)")
    @ApiResponses(ApiResponse(code = 201, message = "Successfully added new device. See 'Location' in headers"),
            ApiResponse(code = 401, message = "Expired or invalid cookie session"),
            ApiResponse(code = 422, message = "Handle with given ID already exists or device name is empty"))
    @PostMapping
    fun addNewHandle(
            @ApiParam(value = "Valid user's session cookie", required = true)
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @RequestBody(required = true) deviceDto: DeviceDto
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val handle = deviceFacade.insert(DeviceDocument(deviceDto.id, deviceDto.name, userId, deviceDto.deviceType))
        return ResponseEntity.created(URI("/users/devices/${handle.id}")).body(null)
    }

    @ApiOperation(value = "Get all user's devices")
    @ApiResponses(ApiResponse(code = 200, message = "Return list of user's devices"))
    @GetMapping
    fun getAllHandles(
            @CookieValue("SESSIONID", required = true) sessionId: String
    ): ResponseEntity<HandleList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val list = deviceFacade.findByUserId(userId)
        return ResponseEntity.ok(HandleList(list))
    }

    @ApiOperation(value = "Get devices by id")
    @ApiResponses(ApiResponse(code = 200, message = "Returns device with given id"),
            ApiResponse(code = 404, message = "Requested resource does not exists"))
    @GetMapping("/{nodeId}")
    fun getByHandleId(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("nodeId", required = true) handleId: String
    ): ResponseEntity<DeviceDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val device: DeviceDocument? = deviceFacade.findById(handleId)
        return if (device != null) {
            ResponseEntity.ok(DeviceDto(device.id, device.name, device.deviceType))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @ApiOperation(value = "Override device if does not exists")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully overridden. Nothing to return"),
            ApiResponse(code = 404, message = "Requested resource does not exists"),
            ApiResponse(code = 422, message = "Handle name was empty"))
    @PutMapping("/{nodeId}")
    fun updateHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable(name = "nodeId", required = true) handleId: String,
            @RequestBody(required = true) deviceDto: DeviceDto
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        deviceFacade.save(DeviceDocument(handleId, deviceDto.name, userId, deviceDto.deviceType))
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation(value = "Override device if does not exists")
    @ApiResponses(ApiResponse(code = 204, message = "Resource was successfully deleted. Nothing to return"))
    @DeleteMapping("/{nodeId}")
    fun deleteHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("nodeId") handleId: String): ResponseEntity<Void> {
        deviceFacade.deleteById(handleId)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(ExistingHandleException::class)
    fun handle(ex: ExistingHandleException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

    @ExceptionHandler(EmptyHandleNameException::class)
    fun handle(ex: EmptyHandleNameException) = ResponseEntity.unprocessableEntity().body(ex.message)!!

}

data class DeviceDto(
        val id: String,
        val name: String,
        val deviceType: DeviceType
)

data class HandleList(
        val devices: List<DeviceDto>
)

