package com.kkoza.starter.testutil

import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.dto.UserDto

class UserBuilder {

    String id = 'any-id'
    String email = 'email@email.com'
    String password = 'any-existingPassword'
    String phoneNumber = '123456789'
    List<String> handles = ['handleAlarmFilterEx-id']

    static UserBuilder create(String id) {
        def builder = new UserBuilder()
        builder.id = id
        return builder
    }

    UserBuilder setEmail(String email) {
        this.email = email
        return this
    }

    UserBuilder setPassword(String password) {
        this.password = password
        return this
    }

    UserBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber
        return this
    }

    UserBuilder setHandles(List<String> handles) {
        this.handles = handles
        return this
    }

    UserDto buildDto() {
        return new UserDto(
                email,
                password,
                phoneNumber
        )
    }

    UserDocument buildDocument() {
        return new UserDocument(
                id,
                email,
                password,
                phoneNumber)
    }

}
