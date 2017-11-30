package com.kkoza.starter.user.exception

class ExistingLoginException(login: String) : RuntimeException("User email = $login already exists") {
}