package io.github.dbstarll.study.service.impl;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.EntityFactory.PojoFields;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation.Position;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.entity.UnitWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.ExerciseWordService;
import io.github.dbstarll.study.service.UnitService;
import io.github.dbstarll.study.service.UnitWordService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.UnitWordServiceAttach;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public final class UnitWordServiceImplemental extends StudyImplementals<UnitWord, UnitWordService>
        implements UnitWordServiceAttach {
    private static final List<Variable<String>> LET_WORD_ID = Collections.singletonList(
            new Variable<>(WordBase.FIELD_NAME_WORD_ID, "$" + WordBase.FIELD_NAME_WORD_ID));
    private static final Bson MATCH_WORD_ID = Filters.expr(Filters.eq("$eq", Arrays.asList(
            "$" + WordBase.FIELD_NAME_WORD_ID, "$$" + WordBase.FIELD_NAME_WORD_ID)));
    private static final Bson PROJECTION_UNIT_WORD = Projections.fields(Projections.excludeId(),
            Projections.exclude("dateCreated", "lastModified", BookBase.FIELD_NAME_BOOK_ID));
    private static final Bson PROJECTION_EXERCISE_WORD = Aggregates
            .project(Projections.fields(Projections.excludeId(), Projections.exclude("lastModified",
                    BookBase.FIELD_NAME_BOOK_ID, Namable.FIELD_NAME_NAME, WordBase.FIELD_NAME_WORD_ID)));

    private WordService wordService;
    private UnitService unitService;
    private ExerciseWordService exerciseWordService;

    /**
     * 构造UnitWordServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public UnitWordServiceImplemental(final UnitWordService service, final Collection<UnitWord> collection) {
        super(service, collection);
    }

    /**
     * 设置WordService.
     *
     * @param wordService WordService
     */
    public void setWordService(final WordService wordService) {
        this.wordService = wordService;
    }

    /**
     * 设置UnitService.
     *
     * @param unitService UnitService
     */
    public void setUnitService(final UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * 设置ExerciseWordService.
     *
     * @param exerciseWordService ExerciseWordService
     */
    public void setExerciseWordService(final ExerciseWordService exerciseWordService) {
        this.exerciseWordService = exerciseWordService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(wordService, "wordService is null");
        notNull(unitService, "unitService is null");
        notNull(exerciseWordService, "exerciseWordService is null");
    }

    @Override
    public MongoIterable<UnitWordWithExercise> findWithExercise(final Bson filter, final ObjectId exerciseBookId) {
        notNull(exerciseBookId, "exerciseBookId not set");
        final Bson filterByExerciseBookId = exerciseWordService.filterByExerciseBookId(exerciseBookId);
        final Bson match = Aggregates.match(Filters.and(filterByExerciseBookId, MATCH_WORD_ID));
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .project(PROJECTION_UNIT_WORD)
                .join(exerciseWordService, LET_WORD_ID, Arrays.asList(match, PROJECTION_EXERCISE_WORD))
                .join(wordService, WordBase.FIELD_NAME_WORD_ID)
                .build()
                .joinOne(DEFAULT_CONTEXT).map(t -> {
                    final UnitWordWithExercise ue = EntityFactory.newInstance(UnitWordWithExercise.class,
                            ((PojoFields) t.getKey()).fields());
                    final ExerciseWord exerciseWord = (ExerciseWord) t.getValue().get(ExerciseWord.class);
                    if (exerciseWord != null) {
                        ue.setExercise(exerciseWord);
                    } else {
                        ue.setWord((Word) t.getValue().get(Word.class));
                    }
                    return ue;
                });
    }

    @Override
    public UnitWord save(final UnitWord entity, final ObjectId newEntityId, final Validate validate) {
        return validateAndSave(entity, newEntityId, validate, new DuplicateValidation());
    }

    /**
     * 检查加入单元的单词是否重复.
     */
    private class DuplicateValidation extends AbstractEntityValidation {
        @Override
        public void validate(final UnitWord entity, final UnitWord original, final Validate validate) {
            if (original == null && !validate.hasErrors()) {
                final Bson filter = Filters.and(service.filterByUnitId(entity.getUnitId()),
                        service.filterByWordId(entity.getWordId()));
                if (service.count(filter) > 0) {
                    validate.addFieldError(WordBase.FIELD_NAME_WORD_ID, "重复的单词");
                }
            }
        }
    }

    /**
     * 自动装配属性.
     *
     * @return packingValidation
     */
    @GeneralValidation(position = Position.FIRST)
    public Validation<UnitWord> packingValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final UnitWord entity, final UnitWord original, final Validate validate) {
                if (original == null) {
                    getEntity(entity.getWordId(), wordService).ifPresent(word -> entity.setName(word.getName()));
                    getEntity(entity.getUnitId(), unitService).ifPresent(unit -> entity.setBookId(unit.getBookId()));
                } else if (!Objects.equals(entity.getName(), original.getName())) {
                    validate.addFieldError(Namable.FIELD_NAME_NAME, "单词设置后不得修改");
                }
            }
        };
    }
}
