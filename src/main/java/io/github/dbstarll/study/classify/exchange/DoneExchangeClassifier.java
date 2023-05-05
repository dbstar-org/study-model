package io.github.dbstarll.study.classify.exchange;

/**
 * 过去式/过去分词的词态变化分类.
 */
class DoneExchangeClassifier implements ExchangeClassifier {
    @Override
    public String classify(final String word, final String exchange) {
        if (exchange.equals(word + "ed")) {
            return null;
        } else if (word.endsWith("e") && exchange.equals(word + "d")) {
            return "eed";
        } else if (exchange.equals(word + word.substring(word.length() - 1) + "ed")) {
            return "2ed";
        } else if (word.endsWith("y") && exchange.equals(word.substring(0, word.length() - 1) + "ied")) {
            return "yed";
        } else if (exchange.equals(word)) {
            return "same";
        } else {
            return "irregular";
        }
    }
}
