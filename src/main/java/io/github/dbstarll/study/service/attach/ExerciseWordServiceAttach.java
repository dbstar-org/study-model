package io.github.dbstarll.study.service.attach;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.service.Implementation;
import io.github.dbstarll.dubai.model.service.ServiceSaver;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.service.impl.ExerciseWordServiceImplemental;
import org.bson.conversions.Bson;

import java.util.regex.Pattern;

@Implementation(ExerciseWordServiceImplemental.class)
public interface ExerciseWordServiceAttach extends StudyAttachs, ServiceSaver<ExerciseWord> {
    /**
     * 按过滤条件，从所有匹配的练习词中，随机抽取出指定数量的练习词.
     *
     * @param filter 过滤条件
     * @param num    抽样数量
     * @return 随机抽取的练习词
     */
    MongoIterable<ExerciseWord> sample(Bson filter, int num);

    /**
     * 按指定的练习词和相似度正则匹配模版来查询相似的干扰词，返回响应的查询条件.
     *
     * @param exerciseWord 练习词
     * @param pattern      相似度正则匹配模版
     * @return 过滤条件
     */
    Bson filterByInterfere(ExerciseWord exerciseWord, Pattern pattern);
}
