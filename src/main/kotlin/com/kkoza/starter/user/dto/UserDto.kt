package com.kkoza.starter.user.dto

data class UserDto(
        val id: String? = null,
        val login: String,
        val password: String,
        val name: String,
        val surname: String,
        val email: String,
        val phoneNumber: String,
        val handles: List<String>
)