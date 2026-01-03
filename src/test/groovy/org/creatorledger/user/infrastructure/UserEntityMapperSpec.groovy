package org.creatorledger.user.infrastructure

import org.creatorledger.user.domain.Email
import org.creatorledger.user.domain.User
import org.creatorledger.user.domain.UserId
import spock.lang.Specification

class UserEntityMapperSpec extends Specification {

    def "should map domain user to JPA entity"() {
        given: "a domain user"
        def userId = UserId.generate()
        def email = Email.of("test@example.com")
        def user = User.register(userId, email)

        when: "mapping to entity"
        def entity = UserEntityMapper.toEntity(user)

        then: "entity is mapped correctly"
        entity != null
        entity.id == userId.value()
        entity.email == email.value()
    }

    def "should map JPA entity to domain user"() {
        given: "a JPA entity"
        def id = UUID.randomUUID()
        def email = "test@example.com"
        def entity = new UserJpaEntity(id, email)

        when: "mapping to domain"
        def user = UserEntityMapper.toDomain(entity)

        then: "user is mapped correctly"
        user != null
        user.id().value() == id
        user.email().value() == email
    }

    def "should handle null when mapping to entity"() {
        when: "mapping null to entity"
        def result = UserEntityMapper.toEntity(null)

        then: "null is returned"
        result == null
    }

    def "should handle null when mapping to domain"() {
        when: "mapping null to domain"
        def result = UserEntityMapper.toDomain(null)

        then: "null is returned"
        result == null
    }

    def "should round-trip correctly"() {
        given: "a domain user"
        def user = User.register(Email.of("roundtrip@example.com"))

        when: "converting to entity and back"
        def entity = UserEntityMapper.toEntity(user)
        def reconstituted = UserEntityMapper.toDomain(entity)

        then: "user is preserved"
        reconstituted.id() == user.id()
        reconstituted.email() == user.email()
    }
}
