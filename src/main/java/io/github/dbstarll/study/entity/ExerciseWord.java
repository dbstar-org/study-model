package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.study.entity.ext.Exchange;
import io.github.dbstarll.study.entity.ext.MasterPercent;
import io.github.dbstarll.study.entity.join.ExerciseBookBase;
import io.github.dbstarll.study.entity.join.WordBase;

import java.util.Map;

@Table
public interface ExerciseWord extends StudyEntities, Cacheable, Namable, ExerciseBookBase, WordBase {
    String FIELD_NAME_EXCHANGES = "exchanges";

    /**
     * 获取练习熟练度列表.
     *
     * @return 练习熟练度列表
     */
    Map<String, MasterPercent> getMasterPercents();

    /**
     * 设置练习熟练度列表.
     *
     * @param masterPercents 练习熟练度列表
     */
    void setMasterPercents(Map<String, MasterPercent> masterPercents);

    /**
     * 获得词态变化列表.
     *
     * @return 词态变化列表
     */
    Map<String, Exchange> getExchanges();

    /**
     * 设置词态变化列表.
     *
     * @param exchanges 词态变化列表
     */
    void setExchanges(Map<String, Exchange> exchanges);
}
