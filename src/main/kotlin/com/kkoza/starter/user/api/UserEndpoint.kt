package com.kkoza.starter.user.api

import com.kkoza.starter.session.InvalidSessionException
import com.kkoza.starter.session.NotExistingUserException
import com.kkoza.starter.session.SessionService
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.user.ValidationResult
import com.kkoza.starter.user.dto.LoginDto
import com.kkoza.starter.user.dto.UserDto
import com.kkoza.starter.user.exception.ExistingEmailException
import com.kkoza.starter.user.exception.ExistingLoginException
import com.kkoza.starter.user.exception.InvalidUserDataException
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.invoke.MethodHandles
import java.net.URI


@RestController
@Api(description = "Register, login, edit user information")
class UserEndpoint(private val userFacade: UserFacade, private val SessionService: SessionService) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    @ApiOperation(value = "Should be used to create new user")
    @ApiResponses(ApiResponse(code = 201, message = "Successfully created new user. See 'Location' in response headers"),
            ApiResponse(code = 422, message = "Email already exists or provided user data are not correct"))
    @PostMapping("/users")
    fun createNewUser(@RequestBody userDto: UserDto): ResponseEntity<Void> {
        val userDocument: UserDocument = userFacade.register(UserDocument(
                null,
                userDto.email,
                userDto.password,
                userDto.phoneNumber,
                emptyList()
        ))
        return ResponseEntity.created(URI("/users/${userDocument.userId}")).build()
    }

    @ApiOperation(value = "Find existing user by ID", response = UserDocument::class)
    @ApiResponses(ApiResponse(code = 200, message = "User was found, see response body", response = UserDocument::class),
            ApiResponse(code = 404, message = "User not found"))
    @GetMapping("/users/{userId}")
    fun findUserById(@PathVariable(name = "userId", required = true) userId: String): ResponseEntity<UserDocument?> {
        val user: UserDocument? = userFacade.findUserById(userId) ?: return ResponseEntity(null, HttpStatus.NOT_FOUND)
        return ResponseEntity.ok().body(user)
    }

    @ApiOperation(value = "Override existing user")
    @ApiResponses(ApiResponse(code = 200, message = "User was successfully overridden."),
            ApiResponse(code = 422, message = "Email already exists or provided user data are not correct"))
    @PutMapping("/users/{userId}")
    fun updateExistingUser(@PathVariable("userId") userId: String,
                           @RequestBody userDto: UserDto): ResponseEntity<Void> {
        userFacade.updateUser(UserDocument(
                userId,
                userDto.email,
                userDto.password,
                userDto.phoneNumber,
                userDto.handles))
        return ResponseEntity.ok(null)
    }

    @ApiOperation(value = "Login with user's credentials to create cookie session")
    @ApiResponses(
            ApiResponse(code = 200, message = "Credentials were found and new session is created"),
            ApiResponse(code = 401, message = "Provided credentials weren't found"))
    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<Void> {
        val user = userFacade.findUserByCredentials(loginDto.email, loginDto.password) ?: throw NotExistingUserException(loginDto.email)
        val session = SessionService.createSession(user.userId!!)
        val headers = HttpHeaders()
        headers.add("Set-Cookie", "SESSIONID=$session")
        return ResponseEntity(headers, HttpStatus.OK)
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

    @ExceptionHandler(NotExistingUserException::class)
    fun handleNotExistingUserException(ex: NotExistingUserException): ResponseEntity<String> {
        logger.info("invalid user credentials: ${ex.message}")
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(InvalidSessionException::class)
    fun handleInvalidSessionException(ex: InvalidSessionException): ResponseEntity<String> {
        logger.info("Invalid session: ${ex.message}")
        return ResponseEntity(ex.message!!, HttpStatus.UNAUTHORIZED)
    }
}

