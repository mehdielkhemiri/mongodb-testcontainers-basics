package org.mel.infrastructure.mongo;

import java.util.Arrays;

public enum PropertyType {
    Apartment("Apartment"), GuestSuite("Guest suite"), House("House");
    private final String value;

    PropertyType(String value) {
        this.value = value;
    }

    public static PropertyType of(String propertyType) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(propertyType))
                .findFirst()
                .orElseThrow();
    }

    public String getValue() {
        return value;
    }
}
