package io.github.dbstarll.study.classify.exchange;

import io.github.dbstarll.study.entity.enums.ExchangeKey;

import java.util.EnumMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public final class ExchangeUtils {
    private static final Map<ExchangeKey, ExchangeClassifier> CLASSIFIERS = new EnumMap<>(ExchangeKey.class);

    static {
        CLASSIFIERS.put(ExchangeKey.PL, new PlExchangeClassifier());
        CLASSIFIERS.put(ExchangeKey.ING, new IngExchangeClassifier());
        CLASSIFIERS.put(ExchangeKey.DONE, new DoneExchangeClassifier());
        CLASSIFIERS.put(ExchangeKey.THIRD, CLASSIFIERS.get(ExchangeKey.PL));
        CLASSIFIERS.put(ExchangeKey.PAST, CLASSIFIERS.get(ExchangeKey.DONE));
        CLASSIFIERS.put(ExchangeKey.ER, new ErExchangeClassifier());
        CLASSIFIERS.put(ExchangeKey.EST, new EstExchangeClassifier());
    }

    private ExchangeUtils() {
        // 禁止实例化
    }

    /**
     * 获得词态变化的分类.
     *
     * @param key      词态变化类型
     * @param word     原型词
     * @param exchange 词态变化后的词
     * @return 词态变化的分类
     */
    public static String classify(final ExchangeKey key, final String word, final String exchange) {
        notNull(key, "ExchangeKey not set");
        notEmpty(word, "word not set");
        notEmpty(exchange, "exchange not set");
        return notNull(CLASSIFIERS.get(key), "classifier not found for key: " + key).classify(word, exchange);
    }
}
