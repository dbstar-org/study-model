package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.study.entity.join.BookBase;

@Table
public interface Unit extends StudyEntities, Cacheable, BookBase {
    /**
     * 获得单元的序号.
     *
     * @return 单元的序号
     */
    String getSn();

    /**
     * 设置单元的序号.
     *
     * @param sn 单元的序号
     */
    void setSn(String sn);

    /**
     * 获得单元的标题.
     *
     * @return 单元的标题
     */
    String getTitle();

    /**
     * 设置单元的标题.
     *
     * @param title 单元的标题
     */
    void setTitle(String title);

    /**
     * 获得单元中单词的数量.
     *
     * @return 单词的数量
     */
    int getWordCount();

    /**
     * 设置单元中单词的数量.
     *
     * @param wordCount 单词的数量
     */
    void setWordCount(int wordCount);
}
