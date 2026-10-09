package com.helpdesk.app.infrastructure.security;

import com.helpdesk.app.domain.model.user.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtActorAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject());
        String email = jwt.getClaimAsString(JwtTokenGeneratorAdapter.CLAIM_EMAIL);
        UserRole role = UserRole.valueOf(jwt.getClaimAsString(JwtTokenGeneratorAdapter.CLAIM_ROLE));

        AuthenticatedActorPrincipal principal = new AuthenticatedActorPrincipal(userId, email, role);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.copyOf(principal.getAuthorities())
        );
    }
}
