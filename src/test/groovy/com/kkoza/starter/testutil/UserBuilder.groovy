package com.kkoza.starter.testutil

import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.dto.UserDto

class UserBuilder {

    String id = 'any-id'
    String login = 'any-login'
    String password = 'any-password'
    String name = 'any-name'
    String surname = 'any-surname'
    String email = 'email@email.com'
    String phoneNumber = '123456789'

    static UserBuilder create(String id) {
        def builder = new UserBuilder()
        builder.id = id
        return builder
    }

    UserBuilder setLogin(String login) {
        this.login = login
        return this
    }

    UserBuilder setPassword(String password) {
        this.password = password
        return this
    }

    UserBuilder setName(String name) {
        this.name = name
        return this
    }

    UserBuilder setSurname(String surname) {
        this.surname = surname
        return this
    }

    UserBuilder setEmail(String email) {
        this.email = email
        return this
    }

    UserBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber
        return this
    }

    UserDto buildDto() {
        return new UserDto(
                id,
                login,
                password,
                name,
                surname,
                email,
                phoneNumber
        )
    }

    UserDocument buildDocument() {
        return new UserDocument(
                id,
                login,
                password,
                name,
                surname,
                email,
                phoneNumber)
    }

}
