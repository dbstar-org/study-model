package io.github.dbstarll.study.service.attach;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.dubai.model.service.Service;
import io.github.dbstarll.dubai.model.service.ServiceSaver;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.service.impl.WordServiceImplemental;
import io.github.dbstarll.study.service.impl.WordServiceImplemental.WordWithJoin;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@Implementation(WordServiceImplemental.class)
public interface WordServiceAttach extends StudyAttachs, ServiceSaver<Word> {
    /**
     * 根据提供的内容进行查询.
     *
     * @param word          匹配的内容
     * @param matchExchange 是否匹配词态变化
     * @param fuzzyMatching 是否模糊匹配
     * @return 过滤条件
     */
    Bson filterByWord(String word, boolean matchExchange, boolean fuzzyMatching);

    /**
     * 与外部的表进行left join查询，返回实体与外部表关联的结果列表.
     *
     * @param filter      过滤条件
     * @param joinService join的服务类
     * @param joinField   外部join字段名
     * @param joinId      join的字段值
     * @param <E1>        join的实体类
     * @param <S1>        join的服务类
     * @return 实体与外部表关联的结果列表
     */
    <E1 extends Entity, S1 extends Service<E1>> MongoIterable<WordWithJoin> findWithJoin(
            Bson filter, S1 joinService, String joinField, ObjectId joinId);
}
