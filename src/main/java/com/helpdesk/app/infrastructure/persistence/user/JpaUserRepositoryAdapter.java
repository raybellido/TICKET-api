package com.helpdesk.app.infrastructure.persistence.user;

import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);

        UserJpaEntity savedEntity = repository.save(entity);

        return UserPersistenceMapper.toDomain(savedEntity);
    }
}
