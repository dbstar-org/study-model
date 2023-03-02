package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.dubai.model.entity.EnumValue;

/**
 * 学历等级(小学/初中/高中等等).
 */
@EnumValue(method = "toString")
public enum SchoolLevel {
    A_PRIMARY("a_primary"),
    B_MIDDLE("b_middle"),
    C_HIGH("c_high"),
    D_BACHELOR("d_bachelor"),
    E_MASTER("e_master"),
    F_DOCTOR("f_doctor");

    private final String name;

    SchoolLevel(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
