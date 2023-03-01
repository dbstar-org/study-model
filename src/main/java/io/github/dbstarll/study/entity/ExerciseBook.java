package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.model.entity.info.Namable;

@Table
public interface ExerciseBook extends StudyEntities, Cacheable, Namable {
    /**
     * 获得单词本包含单词的数量.
     *
     * @return 单词的数量
     */
    int getWordCount();

    /**
     * 设置单词本包含单词的数量.
     *
     * @param wordCount 单词的数量
     */
    void setWordCount(int wordCount);
}
