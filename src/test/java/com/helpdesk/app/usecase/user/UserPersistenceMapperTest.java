package com.helpdesk.app.usecase.user;

import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import com.helpdesk.app.infrastructure.persistence.user.UserJpaEntity;
import com.helpdesk.app.infrastructure.persistence.user.UserPersistenceMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPersistenceMapperTest {


    @Test
    void shouldConvertDomainUserToJpaEntity() {

        User user = new User("Juan", new Email("juan@gmail.com"), "123456");

        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);

        assertEquals("Juan", entity.getName());
        assertEquals("juan@gmail.com", entity.getEmail());
        assertEquals("123456", entity.getPassword());
        assertEquals(UserRole.CUSTOMER, entity.getRole());
    }

    @Test
    void shouldConvertJpaEntityToDomainUser() {

        UserJpaEntity entity = new UserJpaEntity(1L, "Juan", "juan@gmail.com", "123456", UserRole.CUSTOMER);

        User user = UserPersistenceMapper.toDomain(entity);

        assertEquals(1L, user.getId());
        assertEquals("Juan", user.getName());
        assertEquals("juan@gmail.com", user.getEmail().getValue());
        assertEquals(UserRole.CUSTOMER, user.getRole());
    }
}
