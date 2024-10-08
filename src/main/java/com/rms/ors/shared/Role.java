package com.rms.ors.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rms.ors.shared.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN(Set.of(
            USER_CREATE,
            MANAGEMENT_READ,
            MANAGEMENT_UPDATE,
            ADMIN_DELETE_APPLICATION,
            ADMIN_CREATE,
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE_USER)),
    MANAGEMENT(Set.of(
            MANAGEMENT_READ,
            MANAGEMENT_UPDATE
    )), USER(Set.of(
            USER_CREATE,
            USER_READ,
            USER_UPDATE
    ));

    private final Set<Permission> permission;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermission()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_%s".formatted(this.name())));
        return authorities;
    }
}
