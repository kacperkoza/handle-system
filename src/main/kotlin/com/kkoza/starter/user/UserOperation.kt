package com.kkoza.starter.user

import com.kkoza.starter.settings.SettingsService
import com.kkoza.starter.user.exception.ExistingEmailException
import com.kkoza.starter.user.exception.ExistingLoginException


class UserOperation(private val userRepository: UserRepository,
                    private val userValidator: UserValidator,
                    private val settingsService: SettingsService) {

    fun save(userDocument: UserDocument): UserDocument {
        userValidator.validate(userDocument)
        validateUserDocument(userDocument)
        val user = userRepository.save(userDocument)
        settingsService.createDefaultSettings(user.userId!!)
        return user
    }

    fun update(userDocument: UserDocument) {
        userValidator.validate(userDocument)
        userRepository.save(userDocument)
    }

    private fun validateUserDocument(userDocument: UserDocument) {
        throwExceptionIfEmailExists(userDocument)
    }

    private fun throwExceptionIfEmailExists(userDocument: UserDocument) {
        userDocument.email.let {
            if (userRepository.emailExists(it)) {
                throw ExistingEmailException(it)
            }
        }
    }

    fun findUserById(userId: String): UserDocument? {
        return userRepository.findByUserId(userId)
    }

    fun findUserWithHandle(handleId: String): UserDocument? {
        return userRepository.findUserWithHandle(handleId)
    }
}