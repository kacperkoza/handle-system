package com.kkoza.starter.user

import com.kkoza.starter.user.exception.InvalidUserDataException
import org.apache.commons.validator.routines.EmailValidator

class UserValidator {

    private val emailValidator: EmailValidator = EmailValidator.getInstance()

    companion object {
        private const val REQUIRED_PASSWORD_LENGTH = 6
        private const val REQUIRED_PHONE_NUMBER_LENGTH = 9

        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
        const val PHONE_NUMBER_FIELD = "phoneNumber"
    }

    fun validate(userDocument: UserDocument) {
        val errors: MutableMap<String, String> = mutableMapOf()

        userDocument.let {
            if (!emailValidator.isValid(it.email)) {
                errors.put(EMAIL_FIELD, "invalid email")
            }

            if (!isPasswordLengthValid(it.password)) {
                errors.put(PASSWORD_FIELD, "password length < 6")
            }

            if (!isPhoneNumberLengthValid(it.phoneNumber)) {
                errors.put(PHONE_NUMBER_FIELD, "phone number length must be 9")
            }
        }

        if (errorsPresent(errors)) {
            throw InvalidUserDataException(ValidationResult(errors))
        }
    }

    private fun errorsPresent(errors: MutableMap<String, String>) = !errors.isEmpty()

    private fun isPasswordLengthValid(password: String): Boolean = password.length >= REQUIRED_PASSWORD_LENGTH

    private fun isPhoneNumberLengthValid(phoneNumber: String) = phoneNumber.length == REQUIRED_PHONE_NUMBER_LENGTH
}

data class ValidationResult(val errors: Map<String, String>)