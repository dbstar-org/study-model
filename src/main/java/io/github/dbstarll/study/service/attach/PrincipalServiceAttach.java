package io.github.dbstarll.study.service.attach;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.Principal;
import io.github.dbstarll.study.service.impl.PrincipalServiceImplemental;
import org.bson.conversions.Bson;

import java.util.Map.Entry;

@Implementation(PrincipalServiceImplemental.class)
public interface PrincipalServiceAttach extends StudyAttachs {
    /**
     * 与ExerciseBook表进行left join查询，返回Principal与ExerciseBook关联的结果列表.
     *
     * @param filter 过滤条件
     * @return Principal与ExerciseBook关联的结果列表
     */
    MongoIterable<Entry<Principal, ExerciseBook>> findWithExerciseBook(Bson filter);
}
