package io.github.dbstarll.study.classify.exchange;

/**
 * 比较级的词态变化分类.
 */
class ErExchangeClassifier implements ExchangeClassifier {
    @Override
    public String classify(final String word, final String exchange) {
        return classify(word, exchange, "er", "more");
    }

    static String classify(final String word, final String exchange, final String suffix, final String prefix) {
        if (exchange.equals(word + suffix)) {
            return null;
        } else if (exchange.equals(prefix + " " + word)) {
            return null;
        } else if (word.endsWith("y") && exchange.equals(word.substring(0, word.length() - 1) + "i" + suffix)) {
            return "y" + suffix;
        } else if (word.endsWith("e") && exchange.equals(word + suffix.substring(1))) {
            return "e" + suffix;
        } else if (exchange.equals(word + word.substring(word.length() - 1) + suffix)) {
            return "2" + suffix;
        } else if (exchange.equals(word)) {
            return "same";
        } else {
            return "irregular";
        }
    }
}
