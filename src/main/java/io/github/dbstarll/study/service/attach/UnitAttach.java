package io.github.dbstarll.study.service.attach;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.service.impl.UnitAttachImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

@Implementation(UnitAttachImplemental.class)
public interface UnitAttach<E extends StudyEntities & UnitBase> extends StudyAttachs {
    /**
     * 按单元Id来过滤.
     *
     * @param unitId 单元Id
     * @return 过滤条件
     */
    Bson filterByUnitId(ObjectId unitId);

    /**
     * 按单元Id来统计匹配的实体数量.
     *
     * @param unitId 单元Id
     * @return 匹配的实体数量
     */
    long countByUnitId(ObjectId unitId);

    /**
     * 按单元Id来查询匹配的实体列表.
     *
     * @param unitId 单元Id
     * @return 匹配的实体列表
     */
    FindIterable<E> findByUnitId(ObjectId unitId);

    /**
     * 按单元Id来删除所有匹配的实体.
     *
     * @param unitId 单元Id
     * @return 删除结果
     */
    DeleteResult deleteByUnitId(ObjectId unitId);

    /**
     * 与单元表进行left join查询，返回实体与单元关联的结果列表.
     *
     * @param filter 过滤条件
     * @return 实体与单元关联的结果列表
     */
    MongoIterable<Entry<E, Unit>> findWithUnit(Bson filter);
}
