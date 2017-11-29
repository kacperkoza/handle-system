package com.kkoza.starter.user

import org.springframework.stereotype.Component

@Component
class UserFacade(private val userOperation: UserOperation) {

    fun register(userDocument: UserDocument): UserDocument {
        return userOperation.save(userDocument)
    }

    fun updateUser(userDocument: UserDocument) {
        userOperation.save(userDocument)
    }

    fun findUser(userId: String): UserDocument {
        return userOperation.findUserById(userId)
    }

    fun findUserWithHandle(handleId: String): UserDocument? {
        return userOperation.findUserWithHandle(handleId)
    }


}