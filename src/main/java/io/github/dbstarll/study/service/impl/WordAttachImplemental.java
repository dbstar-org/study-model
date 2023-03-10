package io.github.dbstarll.study.service.impl;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.StudyServices;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.WordAttach;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.Validate.notNull;

public final class WordAttachImplemental<E extends StudyEntities & WordBase, S extends StudyServices<E>>
        extends StudyImplementals<E, S> implements WordAttach<E> {
    private WordService wordService;

    /**
     * 构造WordAttachImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public WordAttachImplemental(final S service, final Collection<E> collection) {
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
        notNull(wordService, "wordService not set.");
    }

    @Override
    public Bson filterByWordId(final ObjectId wordId) {
        return eq(WordBase.FIELD_NAME_WORD_ID, wordId);
    }

    @Override
    public long countByWordId(final ObjectId wordId) {
        return service.count(filterByWordId(wordId));
    }

    @Override
    public FindIterable<E> findByWordId(final ObjectId wordId) {
        return service.find(filterByWordId(wordId));
    }

    @Override
    public DeleteResult deleteByWordId(final ObjectId wordId) {
        return getCollection().deleteMany(filterByWordId(wordId));
    }

    @Override
    public MongoIterable<Entry<E, Word>> findWithWord(final Bson filter) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .join(wordService, WordBase.FIELD_NAME_WORD_ID)
                .build()
                .aggregateOne(DEFAULT_CONTEXT)
                .map(e -> EntryWrapper.wrap(e.getKey(), (Word) e.getValue().get(Word.class)));
    }

    @Override
    public DistinctIterable<ObjectId> distinctWordId(final Bson filter) {
        return getCollection().distinct(WordBase.FIELD_NAME_WORD_ID, filter, ObjectId.class);
    }

    /**
     * 单词ID校验.
     *
     * @return finalWordIdValidation
     */
    @GeneralValidation
    public Validation<E> finalWordIdValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final E entity, final E original, final Validate validate) {
                if (original != null) {
                    checkChanged(entity, original, validate);
                } else if (entity.getWordId() == null) {
                    checkWordId(entity, validate);
                } else {
                    final Optional<Word> ow = getEntity(entity.getWordId(), wordService);
                    if (!ow.isPresent()) {
                        validate.addFieldError(WordBase.FIELD_NAME_WORD_ID, "单词不存在");
                    } else if (!ow.get().isCri()) {
                        validate.addFieldError(WordBase.FIELD_NAME_WORD_ID, "这是一个派生词，请添加原型词");
                    }
                }
            }

            private void checkChanged(final E entity, final E original, final Validate validate) {
                if (!Objects.equals(entity.getWordId(), original.getWordId())) {
                    validate.addFieldError(WordBase.FIELD_NAME_WORD_ID, "单词不可更改");
                }
            }

            private void checkWordId(final E entity, final Validate validate) {
                if (!(entity instanceof Word) || !((Word) entity).isCri()) {
                    validate.addFieldError(WordBase.FIELD_NAME_WORD_ID, "单词未设置");
                }
            }
        };
    }
}
