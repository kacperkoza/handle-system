package com.kkoza.starter.user.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(
        val email: String,
        val password: String,
        val phoneNumber: String,
        val handles: List<String>
)