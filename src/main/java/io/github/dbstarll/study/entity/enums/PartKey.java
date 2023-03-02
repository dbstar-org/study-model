package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.dubai.model.entity.EnumValue;

/**
 * 词性词类.
 */
@EnumValue(method = "toString")
public enum PartKey {
    N("n"), // 名词
    V("v"), // 动词
    VT("vt"), // 及物动词
    VI("vi"), // 不及物动词
    ADJ("adj"), // 形容词
    ADV("adv"), // 副词
    PRON("pron"), // 代词
    NUM("num"), // 数词
    ART("art"), // 冠词
    PREP("prep"), // 介词
    INTERJ("interj"), // 叹词
    CONJ("conj"), // 连词
    AUX("aux"), // 助动词
    NA("na"), // 不确定
    ABBR("abbr"), // 略语
    DET("det"), // 定冠词
    INT("_int"), // 感叹词
    LINK_V("link_v"), PHR("phr");

    private final String name;

    PartKey(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
