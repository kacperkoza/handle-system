package com.kkoza.starter.user

open class UserFacade(
        private val userOperation: UserOperation,
        private val userRepository: UserRepository) {

    fun save(userDocument: UserDocument): UserDocument {
        return userOperation.save(userDocument)
    }

    fun update(userDocument: UserDocument) {
        userOperation.update(userDocument)
    }

    fun findUserById(userId: String): UserDocument? {
        return userOperation.findUserById(userId)
    }

    fun findUserByCredentials(login: String, password: String): UserDocument? {
        return userRepository.findUserByCredentials(login, password)
    }

}