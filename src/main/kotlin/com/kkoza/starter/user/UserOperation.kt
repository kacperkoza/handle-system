package com.kkoza.starter.user

import com.kkoza.starter.user.exception.ExistingEmailException
import com.kkoza.starter.user.exception.ExistingLoginException


class UserOperation(private val userRepository: UserRepository,
                    private val userValidator: UserValidator) {

    fun save(userDocument: UserDocument): UserDocument {
        userValidator.validate(userDocument)
        validateUserDocument(userDocument)
        return userRepository.save(userDocument)
    }

    private fun validateUserDocument(userDocument: UserDocument) {
        throwExceptionIfEmailExists(userDocument)
        throwExceptionIfLoginExists(userDocument)
    }

    private fun throwExceptionIfLoginExists(userDocument: UserDocument) {
        userDocument.login.let {
            if (userRepository.loginExists(it)) {
                throw ExistingLoginException(it)
            }
        }
    }

    private fun throwExceptionIfEmailExists(userDocument: UserDocument) {
        userDocument.email.let {
            if (userRepository.emailExists(it)) {
                throw ExistingEmailException(it)
            }
        }
    }

    fun findUserById(userId: String): UserDocument {
        return userRepository.findByUserId(userId)
    }

    fun findUserWithHandle(handleId: String): UserDocument? {
        return userRepository.findUserWithHandle(handleId)
    }
}