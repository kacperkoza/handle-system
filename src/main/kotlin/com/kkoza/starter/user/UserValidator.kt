package com.kkoza.starter.user

import com.kkoza.starter.user.exception.InvalidUserDataException
import org.apache.commons.validator.routines.EmailValidator

class UserValidator {

    private val emailValidator: EmailValidator = EmailValidator.getInstance()

    companion object {
        private const val REQUIRED_LENGTH = 6

        const val EMAIL_FIELD = "email"
        const val LOGIN_FIELD = "login"
        const val PASSWORD_FIELD = "password"
        const val NAME_FIELD = "name"
        const val SURNAME_FIELD = "surname"
        const val PHONE_NUMBER_FIELD = "phoneNumber"
    }

    fun validate(userDocument: UserDocument) {
        val errors: MutableMap<String, String> = mutableMapOf()

        userDocument.let {
            if (!emailValidator.isValid(it.email)) {
                errors.put(EMAIL_FIELD, "invalid email")
            }

            if (!isLengthValid(it.login)) {
                errors.put(LOGIN_FIELD, "login lentgh < 6")
            }

            if (!isLengthValid(it.password)) {
                errors.put(PASSWORD_FIELD, "password length < 6")
            }


            if (it.name.isBlank()) {
                errors.put(NAME_FIELD, "name cannot be empty")
            }

            if (it.surname.isBlank()) {
                errors.put(SURNAME_FIELD, "surname cannot be empty")
            }

            if (it.phoneNumber.length != 9) {
                errors.put(PHONE_NUMBER_FIELD, "phone number length must be 9")
            }
        }

        if (!errors.isEmpty()) {
            throw InvalidUserDataException(ValidationResult(errors))
        }
    }

    private fun isLengthValid(password: String): Boolean = password.length >= REQUIRED_LENGTH
}

data class ValidationResult(val errors: Map<String, String>)