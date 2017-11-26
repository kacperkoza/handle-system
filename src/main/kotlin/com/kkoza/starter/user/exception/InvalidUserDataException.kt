package com.kkoza.starter.user.exception

import com.kkoza.starter.user.ValidationResult

class InvalidUserDataException(val validationResult: ValidationResult) : RuntimeException()