package io.github.dbstarll.study.entity.join;

import io.github.dbstarll.dubai.model.entity.JoinBase;
import org.bson.types.ObjectId;

/**
 * 用于关联所属的单词.
 */
public interface WordBase extends JoinBase {
    String FIELD_NAME_WORD_ID = "wordId";

    /**
     * 获得单词Id.
     *
     * @return 单词Id
     */
    ObjectId getWordId();

    /**
     * 设置单词Id.
     *
     * @param wordId 单词Id
     */
    void setWordId(ObjectId wordId);
}
