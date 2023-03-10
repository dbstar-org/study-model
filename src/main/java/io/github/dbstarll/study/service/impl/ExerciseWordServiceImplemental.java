package io.github.dbstarll.study.service.impl;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation.Position;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.ext.Exchange;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.ExerciseWordService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.ExerciseWordServiceAttach;
import io.github.dbstarll.utils.lang.enums.EnumUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.notNull;

public final class ExerciseWordServiceImplemental extends StudyImplementals<ExerciseWord, ExerciseWordService>
        implements ExerciseWordServiceAttach {
    private WordService wordService;

    /**
     * 构造ExerciseWordServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public ExerciseWordServiceImplemental(final ExerciseWordService service,
                                          final Collection<ExerciseWord> collection) {
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
    public MongoIterable<ExerciseWord> sample(final Bson filter, final int num) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .sample(num)
                .build()
                .aggregate(DEFAULT_CONTEXT).map(Entry::getKey);
    }

    @Override
    public Bson filterByInterfere(final ExerciseWord exerciseWord, final Pattern pattern) {
        final List<Bson> filters = new ArrayList<>();
        filters.add(service.filterByExerciseBookId(exerciseWord.getBookId()));
        filters.add(Filters.nin(Entity.FIELD_NAME_ID, notNull(exerciseWord).getId()));
        if (pattern != null) {
            filters.add(Filters.regex(Namable.FIELD_NAME_NAME, pattern));
        }
        return Filters.and(filters);
    }

    @Override
    public ExerciseWord save(final ExerciseWord entity, final ObjectId newEntityId, final Validate validate) {
        return validateAndSave(entity, newEntityId, validate, new DuplicateValidation());
    }

    /**
     * 检查加入练习册的单词是否重复.
     */
    private class DuplicateValidation extends AbstractEntityValidation {
        @Override
        public void validate(final ExerciseWord entity, final ExerciseWord original, final Validate validate) {
            if (original == null && !validate.hasErrors()) {
                final Bson filter = Filters.and(service.filterByExerciseBookId(entity.getBookId()),
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
    public Validation<ExerciseWord> packingValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final ExerciseWord entity, final ExerciseWord original, final Validate validate) {
                if (original != null) {
                    if (!Objects.equals(entity.getName(), original.getName())) {
                        validate.addFieldError(Namable.FIELD_NAME_NAME, "单词设置后不得修改");
                    }
                    if (!Objects.equals(entity.getExchanges(), original.getExchanges())) {
                        validate.addFieldError(ExerciseWord.FIELD_NAME_EXCHANGES, "单词设置后不得修改");
                    }
                } else {
                    getEntity(entity.getWordId(), wordService).ifPresent(word -> {
                        entity.setName(word.getName());
                        entity.setExchanges(exchanges(word));
                    });
                }
            }

            private Map<String, Exchange> exchanges(final Word word) {
                if (word.getExchanges() != null) {
                    final Map<String, Exchange> exchanges = new HashMap<>();
                    word.getExchanges().stream()
                            .filter(exchange -> exchange.getWord().indexOf(' ') < 0)
                            .forEach(exchange -> exchanges.put(
                                    EnumUtils.name(exchange.getKey()),
                                    new Exchange(exchange.getWord(), exchange.getClassify())));
                    return exchanges.isEmpty() ? null : exchanges;
                } else {
                    return null;
                }
            }
        };
    }
}
