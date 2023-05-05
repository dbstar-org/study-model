package io.github.dbstarll.study.classify.exchange;

/**
 * 复数/第三人称单数的词态变化分类.
 */
class PlExchangeClassifier implements ExchangeClassifier {
    @Override
    public String classify(final String word, final String exchange) {
        if (exchange.equals(word + "s")) {
            if (word.endsWith("y")) {
                return "ys";
            } else if (word.endsWith("o")) {
                return "os";
            } else {
                return null;
            }
        } else if (exchange.equals(word + "es")) {
            if (word.endsWith("s") || word.endsWith("sh") || word.endsWith("ch") || word.endsWith("x")) {
                return "ses";
            } else if (word.endsWith("o")) {
                return "oes";
            }
        } else if (match(word, exchange, "y", "ies")) {
            return "ies";
        } else if (match(word, exchange, "f", "ves")) {
            return "ves";
        } else if (match(word, exchange, "fe", "ves")) {
            return "ves";
        } else if (exchange.equals(word)) {
            return "same";
        }

        return "irregular";
    }

    private boolean match(final String word, final String exchange, final String endsWith, final String suffix) {
        if (word.endsWith(endsWith)) {
            final String wordIntercepted = word.substring(0, word.length() - endsWith.length());
            return exchange.equals(wordIntercepted + suffix);
        } else {
            return false;
        }
    }
}
