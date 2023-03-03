package io.github.dbstarll.study.entity.join;

import io.github.dbstarll.dubai.model.entity.JoinBase;
import org.bson.types.ObjectId;

/**
 * 用于关联所属的练习册.
 */
public interface ExerciseBookBase extends JoinBase {
    String FIELD_NAME_EXERCISE_BOOK_ID = "bookId";

    /**
     * 获得练习册Id.
     *
     * @return 练习册Id
     */
    ObjectId getBookId();

    /**
     * 设置课本Id.
     *
     * @param bookId 练习册Id
     */
    void setBookId(ObjectId bookId);
}
