package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.utils.lang.enums.EnumValue;

@EnumValue(method = "toString")
public enum SubscribeType {
    PAGE("page"), ENTITY("entity");

    private final String name;

    SubscribeType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
