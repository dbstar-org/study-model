package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.utils.lang.enums.EnumValue;

/**
 * 练习的类型.
 */
@EnumValue(method = "toString")
public enum ExerciseKey {
    LISTEN("listen", 3), SPELL("spell", 14), READ("read", 0), WRITE("write", 0);

    private final String title;
    private final int maxLevel;

    ExerciseKey(final String title, final int maxLevel) {
        this.title = title;
        this.maxLevel = maxLevel;
    }

    /**
     * 获得该种练习的最大级别.
     *
     * @return 该种练习的最大级别
     */
    public int maxLevel() {
        return maxLevel;
    }

    @Override
    public String toString() {
        return title;
    }
}
