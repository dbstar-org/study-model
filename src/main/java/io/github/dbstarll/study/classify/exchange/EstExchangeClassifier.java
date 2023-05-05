package io.github.dbstarll.study.classify.exchange;

/**
 * 最高级的词态变化分类.
 */
class EstExchangeClassifier implements ExchangeClassifier {
    @Override
    public String classify(final String word, final String exchange) {
        return ErExchangeClassifier.classify(word, exchange, "est", "most");
    }
}
