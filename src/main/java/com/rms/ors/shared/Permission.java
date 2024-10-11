package com.rms.ors.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),

    MANAGEMENT_CREATE("management:create"),
    MANAGEMENT_READ("management:read"),
    MANAGEMENT_UPDATE("management:update"),
    MANAGEMENT_DELETE("management:delete"),

    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update");

    private final String permission;
}

