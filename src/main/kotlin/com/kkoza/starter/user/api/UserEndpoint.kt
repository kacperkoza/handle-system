package com.kkoza.starter.user.api

import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.user.ValidationResult
import com.kkoza.starter.user.dto.LoginDto
import com.kkoza.starter.user.dto.UserDto
import com.kkoza.starter.user.exception.ExistingEmailException
import com.kkoza.starter.user.exception.ExistingLoginException
import com.kkoza.starter.user.exception.InvalidUserDataException
import org.apache.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.invoke.MethodHandles
import java.net.URI


@RestController
@RequestMapping
class UserEndpoint(private val userFacade: UserFacade) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable(name = "userId", required = true) userId: String): ResponseEntity<UserDocument> {
        return ResponseEntity.ok().body(userFacade.findUser(userId))
    }

    @PostMapping("/register")
    fun register(@RequestBody userDto: UserDto): ResponseEntity<UserDocument> {
        val userDocument: UserDocument = userFacade.register(UserDocument(
                null,
                userDto.login,
                userDto.password,
                userDto.name,
                userDto.surname,
                userDto.email,
                userDto.phoneNumber,
                userDto.handles
        ))
        return ResponseEntity.created(URI("http://localhost:8080/users/${userDocument.userId}")).build()
    }

    @PutMapping("/users")
    fun update(@RequestBody userDto: UserDto): ResponseEntity<Void> {
        userFacade.updateUser(UserDocument(
                userDto.id,
                userDto.login,
                userDto.password,
                userDto.name,
                userDto.surname,
                userDto.email,
                userDto.phoneNumber,
                userDto.handles
        ))
        return ResponseEntity.ok(null)
    }

    @ExceptionHandler(InvalidUserDataException::class)
    fun handleInvalidUserDataException(ex: InvalidUserDataException): ResponseEntity<ValidationResult> {
        logger.info("invalid user data. Errors: ${ex.validationResult}")
        return ResponseEntity.unprocessableEntity().body(ex.validationResult)
    }

    @ExceptionHandler(ExistingEmailException::class)
    fun handleExistingEmailException(ex: ExistingEmailException): ResponseEntity<String> {
        logger.info("Email = ${ex.message} already exists")
        return ResponseEntity.unprocessableEntity().body(ex.message)
    }

    @ExceptionHandler(ExistingLoginException::class)
    fun handleExistingLoginException(ex: ExistingLoginException): ResponseEntity<String> {
        logger.info("invalid user data. Errors: ${ex.message}")
        return ResponseEntity.unprocessableEntity().body(ex.message)
    }
}

