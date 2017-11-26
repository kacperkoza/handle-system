package com.kkoza.starter.user.exception

class ExistingEmailException(email: String) : RuntimeException("User with email = $email already exists")
