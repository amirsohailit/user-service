package com.mytestorg.user.infrastructure.adapter.output.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserEventType {
    USER_CREATED("USER_CREATED"),
    USER_UPDATED("USER_UPDATED"),
    USER_DELETED("USER_DELETED");

    private String value;

    UserEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
