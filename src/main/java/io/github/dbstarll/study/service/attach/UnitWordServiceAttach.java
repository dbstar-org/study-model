package io.github.dbstarll.study.service.attach;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.dubai.model.service.ServiceSaver;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.entity.UnitWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.service.impl.UnitWordServiceImplemental;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@Implementation(UnitWordServiceImplemental.class)
public interface UnitWordServiceAttach extends StudyAttachs, ServiceSaver<UnitWord> {
    /**
     * 与练习表进行left join查询，返回实体与练习表关联的结果列表.
     *
     * @param filter         过滤条件
     * @param exerciseBookId 练习册Id
     * @return 实体与练习表关联的结果列表
     */
    MongoIterable<UnitWordWithExercise> findWithExercise(Bson filter, ObjectId exerciseBookId);

    @Table
    interface UnitWordWithExercise extends UnitWord {
        ExerciseWord getExercise();

        void setExercise(ExerciseWord exercise);

        Word getWord();

        void setWord(Word word);
    }
}
