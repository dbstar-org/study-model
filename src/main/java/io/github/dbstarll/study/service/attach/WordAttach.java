package io.github.dbstarll.study.service.attach;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.impl.WordAttachImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

@Implementation(WordAttachImplemental.class)
public interface WordAttach<E extends StudyEntities & WordBase> extends StudyAttachs {
    /**
     * 按wordId来过滤.
     *
     * @param wordId wordId
     * @return 过滤条件
     */
    Bson filterByWordId(ObjectId wordId);

    /**
     * 按wordId来统计匹配的实体数量.
     *
     * @param wordId wordId
     * @return 匹配的实体数量
     */
    long countByWordId(ObjectId wordId);

    /**
     * 按wordId来查询匹配的实体列表.
     *
     * @param wordId wordId
     * @return 匹配的实体列表
     */
    FindIterable<E> findByWordId(ObjectId wordId);

    /**
     * 按wordId来删除所有匹配的实体.
     *
     * @param wordId wordId
     * @return 删除结果
     */
    DeleteResult deleteByWordId(ObjectId wordId);

    /**
     * 与单词表进行left join查询，返回实体与单词关联的结果列表.
     *
     * @param filter 过滤条件
     * @return 实体与单元关联的结果列表
     */
    MongoIterable<Entry<E, Word>> findWithWord(Bson filter);

    /**
     * 获得去重以后的wordId列表.
     *
     * @param filter 过滤条件
     * @return 去重以后的wordId列表
     */
    DistinctIterable<ObjectId> distinctWordId(Bson filter);
}
