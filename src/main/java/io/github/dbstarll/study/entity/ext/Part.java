package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.dubai.model.entity.Base;
import io.github.dbstarll.study.entity.enums.PartKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.StringJoiner;

/**
 * 词性词类释义.
 */
public final class Part implements Base {
    private static final long serialVersionUID = -1381475250841967402L;

    private List<PartKey> key;
    private List<String> means;

    /**
     * 默认构造函数.
     */
    public Part() {
        // do nothing
    }

    /**
     * 根据现有PartKey和释义来构造.
     *
     * @param key   PartKey的列表
     * @param means 释义的列表
     */
    public Part(final List<PartKey> key, final List<String> means) {
        this.key = key;
        this.means = means;
    }

    /**
     * 获得PartKey的列表.
     *
     * @return PartKey的列表
     */
    public List<PartKey> getKey() {
        return key;
    }

    /**
     * 设置PartKey的列表.
     *
     * @param key PartKey的列表
     */
    public void setKey(final List<PartKey> key) {
        this.key = key;
    }

    /**
     * 获得释义的列表.
     *
     * @return 释义的列表
     */
    public List<String> getMeans() {
        return means;
    }

    /**
     * 设置释义的列表.
     *
     * @param means 释义的列表
     */
    public void setMeans(final List<String> means) {
        this.means = means;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Part)) {
            return false;
        }
        final Part part = (Part) o;
        return new EqualsBuilder()
                .append(getKey(), part.getKey())
                .append(getMeans(), part.getMeans())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getKey())
                .append(getMeans())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Part.class.getSimpleName() + "[", "]")
                .add("key=" + key)
                .add("means=" + means)
                .toString();
    }
}
