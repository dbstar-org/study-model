package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.dubai.model.entity.EnumValue;

/**
 * 语音语系.
 */
@EnumValue(method = "toString")
public enum PhoneticKey {
    EN("en"), // 英式英语
    AM("am"); // 美式英语

    private final String name;

    PhoneticKey(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
