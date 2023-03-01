package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.dubai.model.entity.Base;
import io.github.dbstarll.study.entity.enums.ExchangeKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.StringJoiner;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * 词态变化.
 */
public final class Exchange implements Base {
    private static final long serialVersionUID = -7677978794602431270L;

    private ExchangeKey key;
    private String word;
    private String classify;

    /**
     * 默认构造函数.
     */
    public Exchange() {
        // do nothing
    }

    /**
     * 构造Exchange.
     *
     * @param key      词态变化类型
     * @param word     词态变化后的词
     * @param classify 词态变化的分类
     */
    public Exchange(final ExchangeKey key, final String word, final String classify) {
        setKey(key);
        setWord(word);
        setClassify(classify);
    }

    /**
     * 获得词态变化类型.
     *
     * @return 词态变化类型
     */
    public ExchangeKey getKey() {
        return key;
    }

    /**
     * 设置词态变化类型.
     *
     * @param key 词态变化类型
     */
    public void setKey(final ExchangeKey key) {
        this.key = notNull(key, "key is null");
    }

    /**
     * 获得词态变化后的词.
     *
     * @return 词态变化后的词
     */
    public String getWord() {
        return word;
    }

    /**
     * 设置词态变化后的词.
     *
     * @param word 词态变化后的词
     */
    public void setWord(final String word) {
        this.word = notBlank(word, "word is blank");
    }

    /**
     * 获得词态变化的分类，null表示标准词态变化.
     *
     * @return 词态变化的分类
     */
    public String getClassify() {
        return classify;
    }

    /**
     * 设置词态变化的分类，null表示标准词态变化.
     *
     * @param classify 词态变化的分类
     */
    public void setClassify(final String classify) {
        this.classify = classify;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Exchange)) {
            return false;
        }
        final Exchange exchange = (Exchange) o;
        return new EqualsBuilder()
                .append(getKey(), exchange.getKey())
                .append(getWord(), exchange.getWord())
                .append(getClassify(), exchange.getClassify())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getKey())
                .append(getWord())
                .append(getClassify())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Exchange.class.getSimpleName() + "[", "]")
                .add("key=" + key)
                .add("word='" + word + "'")
                .add("classify='" + classify + "'")
                .toString();
    }
}
