package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.dubai.model.entity.EnumValue;

/**
 * 学期.
 */
@EnumValue(method = "toString")
public enum Term {
    FIRST("first"), SECOND("second");

    private final String name;

    Term(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
