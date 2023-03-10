package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.info.Describable;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.study.entity.enums.ExchangeKey;
import io.github.dbstarll.study.entity.enums.ExerciseKey;
import io.github.dbstarll.study.entity.join.ExerciseBookBase;
import io.github.dbstarll.study.entity.join.WordBase;

import java.util.Date;

@Table
public interface Exercise extends StudyEntities, Namable, Describable, ExerciseBookBase, WordBase {
    /**
     * 获得练习的类型.
     *
     * @return 练习的类型
     */
    ExerciseKey getExerciseKey();

    /**
     * 设置练习的类型.
     *
     * @param exerciseKey 练习的类型
     */
    void setExerciseKey(ExerciseKey exerciseKey);

    /**
     * 获得词态变化类型.
     *
     * @return 词态变化类型
     */
    ExchangeKey getExchangeKey();

    /**
     * 设置词态变化类型.
     *
     * @param exchangeKey 词态变化类型
     */
    void setExchangeKey(ExchangeKey exchangeKey);

    /**
     * 获得当前的级别.
     *
     * @return 当前的级别
     */
    int getLevel();

    /**
     * 设置当前的级别.
     *
     * @param level 当前的级别
     */
    void setLevel(int level);

    /**
     * 获得本次练习是否正确.
     *
     * @return 本次练习是否正确
     */
    boolean isCorrect();

    /**
     * 设置本次练习是否正确.
     *
     * @param correct 本次练习是否正确
     */
    void setCorrect(boolean correct);

    /**
     * 获得连续正确次数.
     *
     * @return 连续正确次数
     */
    int getBingo();

    /**
     * 设置连续正确次数.
     *
     * @param bingo 连续正确次数
     */
    void setBingo(int bingo);

    /**
     * 获得最后一次练习的时间.
     *
     * @return 最后一次练习的时间
     */
    Date getLast();

    /**
     * 设置最后一次练习的时间.
     *
     * @param last 最后一次练习的时间
     */
    void setLast(Date last);
}
