package io.github.dbstarll.study.classify.exchange;

interface ExchangeClassifier {
    /**
     * 获得词态变化的分类.
     *
     * @param word     原型词
     * @param exchange 词态变化后的词
     * @return 词态变化的分类
     */
    String classify(String word, String exchange);
}
