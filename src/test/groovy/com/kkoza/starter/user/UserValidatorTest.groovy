package com.kkoza.starter.user

import com.kkoza.starter.user.exception.InvalidUserDataException
import com.kkoza.starter.testutil.UserBuilder
import spock.lang.Specification

class UserValidatorTest extends Specification {

    def userValidator = new UserValidator()

    def 'should throw exception when email is invalid'() {
        given:
        def user = UserBuilder.create(null).setEmail('invalid').buildDocument()

        when:
        userValidator.validate(user)

        then:
        def ex = thrown(InvalidUserDataException)
        ensureKeyWithInvalidFieldExists(ex, UserValidator.EMAIL_FIELD)
    }

    def 'should throw exception when password length is < 6'() {
        given:
        def user = UserBuilder.create(null).setPassword('passw').buildDocument()

        when:
        userValidator.validate(user)

        then:
        def ex = thrown(InvalidUserDataException)
        ensureKeyWithInvalidFieldExists(ex, UserValidator.PASSWORD_FIELD)
    }

    def 'should throw exception when phone number length is other than 9'() {
        given:
        def user = UserBuilder.create(null).setPhoneNumber('12345678').buildDocument()

        when:
        userValidator.validate(user)

        then:
        def ex = thrown(InvalidUserDataException)
        ensureKeyWithInvalidFieldExists(ex, UserValidator.PHONE_NUMBER_FIELD)
    }

    def 'should not throw exception for valid data'() {
        given:
        def user = UserBuilder.create(null)
                .setEmail('email@gmail.com')
                .setPassword('123456')
                .setPhoneNumber('123456789')
                .buildDocument()

        when:
        userValidator.validate(user)

        then:
        noExceptionThrown()
    }

    boolean ensureKeyWithInvalidFieldExists(InvalidUserDataException ex, String key) {
        return ex.validationResult.errors.containsKey(key)
    }
}
