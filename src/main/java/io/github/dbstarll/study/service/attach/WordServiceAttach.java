package io.github.dbstarll.study.service.attach;

import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.dubai.model.service.ServiceSaver;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.service.impl.WordServiceImplemental;
import io.github.dbstarll.study.service.impl.WordServiceImplemental.WordWithJoin;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.function.Supplier;

@Implementation(WordServiceImplemental.class)
public interface WordServiceAttach extends StudyAttachs, ServiceSaver<Word> {
    /**
     * 根据提供的内容进行查询.
     *
     * @param word          匹配的内容
     * @param matchExchange 是否匹配词类转换
     * @param fuzzyMatching 是否模糊匹配
     * @return 过滤条件
     */
    Bson filterByWord(String word, boolean matchExchange, boolean fuzzyMatching);

    /**
     * 与外部的表进行left join查询，返回实体与外部表关联的结果列表.
     *
     * @param filter    过滤条件
     * @param joinTable 外部join表
     * @param joinField 外部join字段名
     * @param joinId    join的字段值
     * @param query     额外的查询
     * @return 实体与外部表关联的结果列表
     */
    Iterable<WordWithJoin> findWithJoin(Bson filter, String joinTable, String joinField, ObjectId joinId,
                                        Supplier<Collection<Bson>> query);
}
