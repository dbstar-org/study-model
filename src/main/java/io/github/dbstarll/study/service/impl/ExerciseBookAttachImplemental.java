package io.github.dbstarll.study.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.join.ExerciseBookBase;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.StudyServices;
import io.github.dbstarll.study.service.attach.ExerciseBookAttach;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.Validate.notNull;

public final class ExerciseBookAttachImplemental<E extends StudyEntities & ExerciseBookBase, S extends StudyServices<E>>
        extends StudyImplementals<E, S> implements ExerciseBookAttach<E> {
    private ExerciseBookService exerciseBookService;

    /**
     * 构建BookAttachImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public ExerciseBookAttachImplemental(final S service, final Collection<E> collection) {
        super(service, collection);
    }

    /**
     * 设置ExerciseBookService.
     *
     * @param exerciseBookService ExerciseBookService实例
     */
    public void setExerciseBookService(final ExerciseBookService exerciseBookService) {
        this.exerciseBookService = exerciseBookService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(exerciseBookService, "exerciseBookService not set.");
    }

    @Override
    public Bson filterByExerciseBookId(final ObjectId exerciseBookId) {
        return eq(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID, exerciseBookId);
    }

    @Override
    public long countByExerciseBookId(final ObjectId exerciseBookId) {
        return service.count(filterByExerciseBookId(exerciseBookId));
    }

    @Override
    public FindIterable<E> findByExerciseBookId(final ObjectId exerciseBookId) {
        return service.find(filterByExerciseBookId(exerciseBookId));
    }

    @Override
    public DeleteResult deleteByExerciseBookId(final ObjectId exerciseBookId) {
        return getCollection().deleteMany(filterByExerciseBookId(exerciseBookId));
    }

    @Override
    public MongoIterable<Entry<E, ExerciseBook>> findWithExerciseBook(final Bson filter) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .join(exerciseBookService, ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID)
                .build()
                .joinOne(DEFAULT_CONTEXT)
                .map(e -> EntryWrapper.wrap(e.getKey(), (ExerciseBook) e.getValue().get(ExerciseBook.class)));
    }

    /**
     * 练习册Id校验.
     *
     * @return finalExerciseBookIdValidation
     */
    @GeneralValidation
    public Validation<E> finalExerciseBookIdValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final E entity, final E original, final Validate validate) {
                if (entity.getBookId() == null) {
                    validate.addFieldError(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID, "练习册未设置");
                } else if (original != null && !entity.getBookId().equals(original.getBookId())) {
                    validate.addFieldError(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID, "练习册不可更改");
                } else if (!getEntity(entity.getBookId(), exerciseBookService).isPresent()) {
                    validate.addFieldError(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID, "练习册不存在");
                }
            }
        };
    }
}
