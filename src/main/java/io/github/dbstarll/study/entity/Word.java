package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.study.entity.ext.Exchange;
import io.github.dbstarll.study.entity.ext.Part;
import io.github.dbstarll.study.entity.ext.Phonetic;
import io.github.dbstarll.study.entity.join.WordBase;

import java.util.Set;

@Table
public interface Word extends StudyEntities, Cacheable, Namable, WordBase {
    /**
     * 返回是否基础词汇；true=基础词汇；false=词态变化后的衍生词.
     *
     * @return 是否是基础词汇
     */
    boolean isCri();

    /**
     * 设置是否基础词汇.
     *
     * @param cri 是否基础词汇
     */
    void setCri(boolean cri);

    /**
     * 获得语系语音列表.
     *
     * @return 语系语音列表
     */
    Set<Phonetic> getPhonetics();

    /**
     * 设置语系语音列表.
     *
     * @param phonetics 语系语音列表
     */
    void setPhonetics(Set<Phonetic> phonetics);

    /**
     * 获得词态变化列表.
     *
     * @return 词态变化列表
     */
    Set<Exchange> getExchanges();

    /**
     * 设置词态变化列表.
     *
     * @param exchanges 词态变化列表
     */
    void setExchanges(Set<Exchange> exchanges);

    /**
     * 获得词性词类释义列表.
     *
     * @return 词性词类释义列表
     */
    Set<Part> getParts();

    /**
     * 设置词性词类释义列表.
     *
     * @param parts 词性词类释义列表
     */
    void setParts(Set<Part> parts);
}
