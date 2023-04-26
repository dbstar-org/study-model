package io.github.dbstarll.study.service.impl;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.info.Describable;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation.Position;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.Exercise;
import io.github.dbstarll.study.service.ExerciseService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.ExerciseServiceAttach;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map.Entry;

import static org.apache.commons.lang3.Validate.notNull;

public final class ExerciseServiceImplemental extends StudyImplementals<Exercise, ExerciseService>
        implements ExerciseServiceAttach {
    private static final String SUM_FIELD = "count";
    private WordService wordService;

    /**
     * 构造ExerciseServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public ExerciseServiceImplemental(final ExerciseService service, final Collection<Exercise> collection) {
        super(service, collection);
    }

    /**
     * 设置WordService.
     *
     * @param wordService WordService实例
     */
    public void setWordService(final WordService wordService) {
        this.wordService = wordService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(wordService, "wordService is null");
    }

    @Override
    public MongoIterable<Entry<String, Integer>> countErrors(final Exercise exercise) {
        final Bson filter = Filters.and(
                service.filterByExerciseBookId(exercise.getBookId()),
                service.filterByWordId(exercise.getWordId()),
                Filters.eq("exerciseKey", exercise.getExerciseKey()),
                Filters.eq("correct", false),
                exercise.getExchangeKey() == null
                        ? Filters.exists("exchangeKey", false)
                        : Filters.eq("exchangeKey", exercise.getExchangeKey())
        );
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .group("$" + Describable.FIELD_NAME_DESCRIPTION, Accumulators.sum(SUM_FIELD, 1))
                .sort(Sorts.descending(SUM_FIELD))
                .build()
                .aggregate(Document.class)
                .map(doc -> EntryWrapper.wrap(doc.getString(Entity.FIELD_NAME_ID), doc.getInteger(SUM_FIELD)));
    }

    /**
     * 自动装配属性.
     *
     * @return packingValidation
     */
    @GeneralValidation(position = Position.FIRST)
    public Validation<Exercise> packingValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final Exercise entity, final Exercise original, final Validate validate) {
                if (original == null) {
                    getEntity(entity.getWordId(), wordService).ifPresent(word -> entity.setName(word.getName()));
                } else if (!entity.equals(original)) {
                    validate.addActionError("练习结果不得修改");
                }
            }
        };
    }
}
