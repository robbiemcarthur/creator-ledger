package org.creatorledger.user.infrastructure

import org.creatorledger.user.domain.Email
import org.creatorledger.user.domain.User
import org.creatorledger.user.domain.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@Transactional
@Testcontainers
class JpaUserRepositoryIntegrationSpec extends Specification {

    @Shared
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl())
        registry.add("spring.datasource.username", () -> postgres.getUsername())
        registry.add("spring.datasource.password", () -> postgres.getPassword())
    }

    @Autowired
    JpaUserRepository repository

    def "should save and retrieve a user"() {
        given: "a new user"
        def user = User.register(Email.of("test@example.com"))

        when: "saving the user"
        def saved = repository.save(user)

        and: "retrieving the user by ID"
        def retrieved = repository.findById(user.id())

        then: "the user is persisted and retrieved correctly"
        saved != null
        saved.id() == user.id()
        saved.email() == user.email()

        and: "retrieved user matches saved user"
        retrieved.isPresent()
        retrieved.get().id() == user.id()
        retrieved.get().email() == user.email()
    }

    def "should return empty when user not found"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        when: "finding by ID"
        def result = repository.findById(userId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should check user existence"() {
        given: "a saved user"
        def user = User.register(Email.of("exists@example.com"))
        repository.save(user)

        when: "checking if user exists"
        def exists = repository.existsById(user.id())

        then: "true is returned"
        exists
    }

    def "should return false for non-existent user"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        when: "checking existence"
        def exists = repository.existsById(userId)

        then: "false is returned"
        !exists
    }

    def "should delete a user"() {
        given: "a saved user"
        def user = User.register(Email.of("delete@example.com"))
        repository.save(user)

        when: "deleting the user"
        repository.delete(user)

        and: "checking if user still exists"
        def exists = repository.existsById(user.id())

        then: "user is deleted"
        !exists
    }

    def "should update an existing user"() {
        given: "a saved user"
        def userId = UserId.generate()
        def originalUser = User.register(userId, Email.of("original@example.com"))
        repository.save(originalUser)

        when: "updating the user with new email"
        def updatedUser = User.register(userId, Email.of("updated@example.com"))
        repository.save(updatedUser)

        and: "retrieving the user"
        def retrieved = repository.findById(userId)

        then: "the user is updated"
        retrieved.isPresent()
        retrieved.get().email() == Email.of("updated@example.com")
    }
}
