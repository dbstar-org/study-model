package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.dubai.model.entity.EnumValue;

/**
 * 词态变化类型.
 */
@EnumValue(method = "toString")
public enum ExchangeKey {
    PL("pl"), // 复数
    THIRD("third"), // 第三人称单数
    PAST("past"), // 过去式
    DONE("done"), // 过去分词
    ING("ing"), // 现在分词
    ER("er"), // 比较级
    EST("est"); // 最高级

    private final String name;

    ExchangeKey(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
