package com.rms.ors.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE_USER("admin:delete-user"),
    ADMIN_DELETE_APPLICATION("admin:delete-application"),

    MANAGEMENT_READ("management:read"),
    MANAGEMENT_UPDATE("management:update"),

    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update");

    private final String permission;
}

