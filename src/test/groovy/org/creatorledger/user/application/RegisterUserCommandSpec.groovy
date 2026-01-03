package org.creatorledger.user.application

import spock.lang.Specification

class RegisterUserCommandSpec extends Specification {

    def "should create a valid register user command"() {
        given: "a valid email"
        def email = "user@example.com"

        when: "a command is created"
        def command = new RegisterUserCommand(email)

        then: "it is created successfully"
        command != null
        command.email() == email
    }

    def "should reject null email"() {
        when: "creating command with null email"
        new RegisterUserCommand(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Email cannot be null or blank"
    }

    def "should reject blank email"() {
        when: "creating command with blank email"
        new RegisterUserCommand("   ")

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Email cannot be null or blank"
    }

    def "should be equal when emails are the same"() {
        given: "two commands with the same email"
        def command1 = new RegisterUserCommand("user@example.com")
        def command2 = new RegisterUserCommand("user@example.com")

        expect: "they are equal"
        command1 == command2
        command1.hashCode() == command2.hashCode()
    }

    def "should not be equal when emails differ"() {
        given: "two commands with different emails"
        def command1 = new RegisterUserCommand("user1@example.com")
        def command2 = new RegisterUserCommand("user2@example.com")

        expect: "they are not equal"
        command1 != command2
    }

    def "should provide readable toString"() {
        given: "a command"
        def command = new RegisterUserCommand("user@example.com")

        when: "toString is called"
        def result = command.toString()

        then: "it contains the email"
        result.contains("RegisterUserCommand")
        result.contains("user@example.com")
    }
}
