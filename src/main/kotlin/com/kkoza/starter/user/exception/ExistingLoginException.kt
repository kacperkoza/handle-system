package com.kkoza.starter.user.exception

class ExistingLoginException(login: String) : RuntimeException("User login = $login already exists") {
}