package com.kkoza.starter.handles

import com.kkoza.starter.session.SessionService
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users/handles")
class HandleEndpoint(
        private val handleFacade: HandleFacade,
        private val sessionService: SessionService
) {

    @PostMapping
    fun addNewHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @RequestBody(required = true) handleDto: HandleDto
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val handle = handleFacade.insert(HandleDocument(handleDto.id, handleDto.name, userId))
        return ResponseEntity.created(URI("/users/handles/${handle.id}")).body(null)
    }

    @GetMapping
    fun getAllHandles(
            @CookieValue("SESSIONID", required = true) sessionId: String
    ): ResponseEntity<HandleList> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        val list = handleFacade.findByUserId(userId)
        return ResponseEntity.ok(HandleList(list))
    }

    @GetMapping("/{handleId}")
    fun getByHandleId(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("handleId", required = true) handleId: String
    ): ResponseEntity<HandleDto> {
        sessionService.findUserIdAndUpdateSession(sessionId)
        val handle: HandleDocument? = handleFacade.findById(handleId)
        return if (handle != null) {
            ResponseEntity.ok(HandleDto(handle.id, handle.name))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{handleId}")
    fun updateHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable(name = "handleId", required = true) handleId: String,
            @RequestBody(required = true) name: String
    ): ResponseEntity<Void> {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
        handleFacade.save(HandleDocument(handleId, name, userId))
        return ResponseEntity.ok().body(null)
    }

    @DeleteMapping("/{handleId}")
    fun deleteHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("handleId") handleId: String): ResponseEntity<Void> {
        handleFacade.deleteById(handleId)
        return ResponseEntity.ok().body(null)
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
        val handles: List<HandleDto>
)

data class HandleDocument(

        @Id
        @Field(ID)
        val id: String,

        @Field(HANDLE_NAME)
        val name: String,

        @Field(USER_ID)
        val userId: String

) {
    companion object {
        const val USER_ID = "user_id"
        const val HANDLE_NAME = "handle_name"
        const val ID = "_id"
    }

    fun toDto(): HandleDto = HandleDto(id, name)
}