package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.study.entity.enums.PhoneticKey;
import io.github.dbstarll.study.entity.info.Voiceable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

import java.util.StringJoiner;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * 语系语音.
 */
public final class Phonetic implements Voiceable {
    private static final long serialVersionUID = 7083794416702339433L;

    private PhoneticKey key;
    private String symbol;
    private ObjectId voiceId;
    private transient byte[] mp3 = null;

    /**
     * 默认的构造函数.
     */
    public Phonetic() {
        // do nothing
    }

    /**
     * 构造Phonetic.
     *
     * @param key    语音语系
     * @param symbol 音标
     */
    public Phonetic(final PhoneticKey key, final String symbol) {
        setKey(key);
        setSymbol(symbol);
    }

    /**
     * 获得语音语系.
     *
     * @return 语音语系
     */
    public PhoneticKey getKey() {
        return key;
    }

    /**
     * 设置语音语系.
     *
     * @param key 语音语系
     */
    public void setKey(final PhoneticKey key) {
        this.key = notNull(key, "key is null");
    }

    /**
     * 获得音标.
     *
     * @return 音标
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 设置音标.
     *
     * @param symbol 音标
     */
    public void setSymbol(final String symbol) {
        this.symbol = notBlank(symbol, "symbol is blank");
    }

    @Override
    public ObjectId getVoiceId() {
        return voiceId;
    }

    @Override
    public void setVoiceId(final ObjectId voiceId) {
        this.voiceId = notNull(voiceId, "voiceId is null");
    }

    /**
     * 获得mp3音频数据.
     *
     * @return mp3音频数据
     */
    public byte[] mp3() {
        return mp3;
    }

    /**
     * 根据mp3音频数据来构建Phonetic.
     *
     * @param data mp3音频数据
     * @return Phonetic实例.
     */
    public Phonetic mp3(final byte[] data) {
        this.mp3 = notNull(data, "mp3 data is null");
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Phonetic)) {
            return false;
        }
        final Phonetic phonetic = (Phonetic) o;
        return new EqualsBuilder()
                .append(getKey(), phonetic.getKey())
                .append(getSymbol(), phonetic.getSymbol())
                .append(getVoiceId(), phonetic.getVoiceId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getKey())
                .append(getSymbol())
                .append(getVoiceId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Phonetic.class.getSimpleName() + "[", "]")
                .add("key=" + key)
                .add("symbol='" + symbol + "'")
                .add("voiceId=" + voiceId)
                .add("mp3=" + (mp3() == null ? 0 : mp3().length))
                .toString();
    }
}
