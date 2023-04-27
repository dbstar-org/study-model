package io.github.dbstarll.study.classify.exchange;

/**
 * 现在分词的词态变化分类.
 */
class IngExchangeClassifier implements ExchangeClassifier {
    @Override
    public String classify(final String word, final String exchange) {
        if (exchange.equals(word + "ing")) {
            return null;
        } else if (word.endsWith("e") && exchange.equals(word.substring(0, word.length() - 1) + "ing")) {
            return "eing";
        } else if (exchange.equals(word + word.substring(word.length() - 1) + "ing")) {
            return "2ing";
        } else if (word.endsWith("ie") && exchange.equals(word.substring(0, word.length() - 2) + "ying")) {
            return "ying";
        } else if (exchange.equals(word)) {
            return "same";
        } else {
            return "irregular";
        }
    }
}
