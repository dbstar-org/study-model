package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.utils.lang.enums.EnumValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@EnumValue(method = "toString")
public enum Page {
    BOOK("book", Module.ENGLISH),
    EXERCISE_BOOK("exercise_book", Module.ENGLISH, Mode.USER),
    SPELL("spell", Module.ENGLISH, Mode.USER, Mode.GUEST),
    SPELL_EXCHANGE("spell_exchange", Module.ENGLISH, Mode.USER),

    USER("user", Module.SETTING);

    private final String name;
    private final Module module;
    private final Set<Mode> modes;

    Page(final String name, final Module module, final Mode... modes) {
        this.name = name;
        this.module = module;
        this.modes = new HashSet<>(Arrays.asList(modes));
    }

    /**
     * 获得Page所属的Module.
     *
     * @return module
     */
    public Module getModule() {
        return module;
    }

    /**
     * 检查指定的用户模式是否能访问page.
     *
     * @param mode 用户模式
     * @return 是否能访问page
     */
    public boolean allowMode(final Mode mode) {
        return mode == Mode.ADMIN || modes.contains(mode);
    }

    @Override
    public String toString() {
        return name;
    }
}
