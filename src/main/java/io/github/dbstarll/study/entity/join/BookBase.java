package io.github.dbstarll.study.entity.join;

import io.github.dbstarll.dubai.model.entity.JoinBase;
import org.bson.types.ObjectId;

/**
 * 用于关联所属的课本.
 */
public interface BookBase extends JoinBase {
    String FIELD_NAME_BOOK_ID = "bookId";

    /**
     * 获得课本Id.
     *
     * @return 课本Id
     */
    ObjectId getBookId();

    /**
     * 设置课本Id.
     *
     * @param bookId 课本Id
     */
    void setBookId(ObjectId bookId);
}
