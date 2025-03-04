package com.example.employee_mgmt.config;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtConverter extends JwtAuthenticationConverter {

    public KeycloakJwtConverter() {
        super();
        setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        setPrincipalClaimName("preferred_username");
    }

    private static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            return defaultGrantedAuthoritiesConverter.convert(jwt);
        }
    }
}
