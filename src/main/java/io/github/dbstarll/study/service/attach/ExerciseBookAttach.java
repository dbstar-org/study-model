package io.github.dbstarll.study.service.attach;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.join.ExerciseBookBase;
import io.github.dbstarll.study.service.impl.ExerciseBookAttachImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

@Implementation(ExerciseBookAttachImplemental.class)
public interface ExerciseBookAttach<E extends StudyEntities & ExerciseBookBase> extends StudyAttachs {
    /**
     * 按练习册Id来过滤.
     *
     * @param exerciseBookId 练习册Id
     * @return 过滤条件
     */
    Bson filterByExerciseBookId(ObjectId exerciseBookId);

    /**
     * 按练习册Id来统计匹配的实体数量.
     *
     * @param exerciseBookId 练习册Id
     * @return 匹配的实体数量
     */
    long countByExerciseBookId(ObjectId exerciseBookId);

    /**
     * 按练习册Id来查询匹配的实体列表.
     *
     * @param exerciseBookId 练习册Id
     * @return 匹配的实体列表
     */
    FindIterable<E> findByExerciseBookId(ObjectId exerciseBookId);

    /**
     * 按练习册Id来删除所有匹配的实体.
     *
     * @param exerciseBookId 练习册Id
     * @return 删除结果
     */
    DeleteResult deleteByExerciseBookId(ObjectId exerciseBookId);

    /**
     * 与练习册表进行left join查询，返回实体与练习册关联的结果列表.
     *
     * @param filter 过滤条件
     * @return 实体与练习册关联的结果列表
     */
    MongoIterable<Entry<E, ExerciseBook>> findWithExerciseBook(Bson filter);
}
