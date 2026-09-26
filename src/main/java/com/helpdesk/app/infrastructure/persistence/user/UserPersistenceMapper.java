package com.helpdesk.app.infrastructure.persistence.user;

import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;

public class UserPersistenceMapper {

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail().getValue(),
                user.getPassword(),
                user.getRole()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getPassword(),
                entity.getRole()
        );
    }
}
