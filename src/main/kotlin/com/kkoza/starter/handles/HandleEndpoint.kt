package com.kkoza.starter.handles

import com.kkoza.starter.session.SessionService
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    ) {
        val userId = sessionService.findUserIdAndUpdateSession(sessionId)
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
        val handle = handleFacade.findById(handleId)
        return ResponseEntity.ok(handle)
    }

    @PutMapping("/{handleId}")
    fun updateHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable(name = "handleId", required = true) handleId: String,
            @RequestBody(required = true) name: String
    ): ResponseEntity<Void> {

        return ResponseEntity.ok().body(null)
    }

    @DeleteMapping("/{handleId}")
    fun deleteHandle(
            @CookieValue("SESSIONID", required = true) sessionId: String,
            @PathVariable("handleId") handleId: String): ResponseEntity<Void> {
        handleFacade.deleteById(handleId)
        return ResponseEntity.ok().body(null)
    }
}

data class HandleDto(
        val id: String,
        val name: String
)

data class HandleList(
        val handles: List<HandleDto>
)


class HandleDocument(

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
}