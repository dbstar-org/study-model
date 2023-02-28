package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.study.entity.enums.CefrLevel;
import io.github.dbstarll.study.entity.enums.SchoolLevel;
import io.github.dbstarll.study.entity.enums.Term;

@Table
public interface Book extends StudyEntities, Namable {
    /**
     * 获取欧洲语言共同参考标准等级.
     *
     * @return 欧洲语言共同参考标准等级
     */
    CefrLevel getCefr();

    /**
     * 设置欧洲语言共同参考标准等级.
     *
     * @param cefrLevel 欧洲语言共同参考标准等级
     */
    void setCefr(CefrLevel cefrLevel);

    /**
     * 获得学历等级.
     *
     * @return 学历等级
     */
    SchoolLevel getSchool();

    /**
     * 设置学历等级.
     *
     * @param schoolLevel 学历等级
     */
    void setSchool(SchoolLevel schoolLevel);

    /**
     * 获得年级.
     *
     * @return 年级
     */
    int getGrade();

    /**
     * 设置年级.
     *
     * @param grade 年级
     */
    void setGrade(int grade);

    /**
     * 获得学期.
     *
     * @return 学期
     */
    Term getTerm();

    /**
     * 设置学期.
     *
     * @param term 学期
     */
    void setTerm(Term term);

    /**
     * 获得单元名称的前缀.
     *
     * @return 单元名称的前缀
     */
    String getPrefix();

    /**
     * 设置单元名称的前缀.
     *
     * @param prefix 单元名称的前缀
     */
    void setPrefix(String prefix);

    /**
     * 获得课本包含的单元数量.
     *
     * @return 单元数量
     */
    int getUnitCount();

    /**
     * 设置课本包含的单元数量.
     *
     * @param unitCount 单元数量
     */
    void setUnitCount(int unitCount);

    /**
     * 获得课本包含单词的数量.
     *
     * @return 单词的数量
     */
    int getWordCount();

    /**
     * 设置课本包含单词的数量.
     *
     * @param wordCount 单词的数量
     */
    void setWordCount(int wordCount);
}
