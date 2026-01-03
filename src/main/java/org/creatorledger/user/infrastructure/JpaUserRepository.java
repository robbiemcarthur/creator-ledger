package org.creatorledger.user.infrastructure;

import org.creatorledger.user.application.UserRepository;
import org.creatorledger.user.domain.User;
import org.creatorledger.user.domain.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA implementation of the UserRepository.
 * <p>
 * This is an adapter that bridges the application layer's repository port
 * with Spring Data JPA infrastructure. It handles conversion between domain
 * objects and JPA entities using the UserEntityMapper.
 * </p>
 */
@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springDataRepository;

    public JpaUserRepository(SpringDataUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserEntityMapper.toEntity(user);
        UserJpaEntity saved = springDataRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springDataRepository.findById(id.value())
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return springDataRepository.existsById(id.value());
    }

    @Override
    public void delete(User user) {
        springDataRepository.deleteById(user.id().value());
    }
}
