package io.github.dbstarll.study.entity.join;

import io.github.dbstarll.dubai.model.entity.JoinBase;
import org.bson.types.ObjectId;

/**
 * 用于关联所属的单词本/课本.
 */
public interface BookBase extends JoinBase {
    String FIELD_NAME_BOOK_ID = "bookId";

    /**
     * 获得bookId.
     *
     * @return bookId
     */
    ObjectId getBookId();

    /**
     * 设置bookId.
     *
     * @param bookId bookId
     */
    void setBookId(ObjectId bookId);
}
