package io.github.dbstarll.study.service.attach;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.Book;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.service.impl.BookAttachImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

@Implementation(BookAttachImplemental.class)
public interface BookAttach<E extends StudyEntities & BookBase> extends StudyAttachs {
    /**
     * 按课本Id来过滤.
     *
     * @param bookId 课本Id
     * @return 过滤条件
     */
    Bson filterByBookId(ObjectId bookId);

    /**
     * 按课本Id来统计匹配的实体数量.
     *
     * @param bookId 课本Id
     * @return 匹配的实体数量
     */
    long countByBookId(ObjectId bookId);

    /**
     * 按课本Id来查询匹配的实体列表.
     *
     * @param bookId 课本Id
     * @return 匹配的实体列表
     */
    FindIterable<E> findByBookId(ObjectId bookId);

    /**
     * 按课本Id来删除所有匹配的实体.
     *
     * @param bookId 课本Id
     * @return 删除结果
     */
    DeleteResult deleteByBookId(ObjectId bookId);

    /**
     * 与课本表进行left join查询，返回实体与课本关联的结果列表.
     *
     * @param filter 过滤条件
     * @return 实体与课本关联的结果列表
     */
    MongoIterable<Entry<E, Book>> findWithBook(Bson filter);
}
