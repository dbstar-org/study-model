package io.github.dbstarll.study.service.attach;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.dubai.model.service.Service;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.service.impl.BookAttachImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

@Implementation(BookAttachImplemental.class)
public interface BookAttach<E extends StudyEntities & BookBase> extends StudyAttachs {
    /**
     * 按bookId来过滤.
     *
     * @param bookId bookId
     * @return 过滤条件
     */
    Bson filterByBookId(ObjectId bookId);

    /**
     * 按bookId来统计匹配的实体数量.
     *
     * @param bookId bookId
     * @return 匹配的实体数量
     */
    long countByBookId(ObjectId bookId);

    /**
     * 按bookId来查询匹配的实体列表.
     *
     * @param bookId bookId
     * @return 匹配的实体列表
     */
    FindIterable<E> findByBookId(ObjectId bookId);

    /**
     * 按bookId来删除所有匹配的实体.
     *
     * @param bookId bookId
     * @return 删除结果
     */
    DeleteResult deleteByBookId(ObjectId bookId);

    /**
     * 与外部的单词本表进行left join查询，返回实体与外部单词本关联的结果列表.
     *
     * @param bookService 外部单词本服务
     * @param filter      过滤条件
     * @param <E1>        外部关联单词本的实体类
     * @param <S1>        外部关联单词本的服务类
     * @return 实体与外部单词本关联的结果列表
     */
    <E1 extends Entity, S1 extends Service<E1>> MongoIterable<Entry<E, E1>> findWithBook(S1 bookService, Bson filter);
}
