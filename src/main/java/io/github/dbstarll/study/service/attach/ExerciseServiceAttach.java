package io.github.dbstarll.study.service.attach;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.study.entity.Exercise;
import io.github.dbstarll.study.service.impl.ExerciseServiceImplemental;

import java.util.Map.Entry;

@Implementation(ExerciseServiceImplemental.class)
public interface ExerciseServiceAttach extends StudyAttachs {
    /**
     * 统计对指定单词的练习错误类型汇总.
     *
     * @param exercise 被统计的练习
     * @return 错误类型汇总
     */
    MongoIterable<Entry<String, Integer>> countErrors(Exercise exercise);
}
