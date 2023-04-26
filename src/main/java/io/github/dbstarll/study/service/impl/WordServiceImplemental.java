package io.github.dbstarll.study.service.impl;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.EntityFactory.PojoFields;
import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.Service;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.Voice;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.ext.Phonetic;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.WordServiceAttach;
import org.apache.commons.collections.CollectionUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public final class WordServiceImplemental extends StudyImplementals<Word, WordService> implements WordServiceAttach {
    private static final List<Variable<String>> LET_WORD_ID = new LinkedList<>();

    static {
        LET_WORD_ID.add(new Variable<>(WordBase.FIELD_NAME_WORD_ID, "$" + Entity.FIELD_NAME_ID));
    }

    private static final Bson MATCH_WORD_ID = Filters.expr(
            Filters.eq("$eq",
                    Arrays.asList("$" + WordBase.FIELD_NAME_WORD_ID, "$$" + WordBase.FIELD_NAME_WORD_ID)));
    private static final Bson PROJECTION_WORD = Projections.exclude("dateCreated", "lastModified");
    private static final Bson PROJECTION_JOIN_WORD = Aggregates.project(Projections.include(Entity.FIELD_NAME_ID));

    private VoiceService voiceService;

    /**
     * 构建WordServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public WordServiceImplemental(final WordService service, final Collection<Word> collection) {
        super(service, collection);
    }

    /**
     * 设置VoiceService.
     *
     * @param voiceService VoiceService实例
     */
    public void setVoiceService(final VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(voiceService, "voiceService is null");
    }

    @Override
    public Word save(final Word entity, final ObjectId newEntityId, final Validate validate) {
        return validateAndSave(entity, newEntityId, validate, voiceSaverValidation(newEntityId));
    }

    @Override
    public Bson filterByWord(final String word, final boolean matchExchange, final boolean fuzzyMatching) {
        notBlank(word);

        final Bson nameFilter;
        if (fuzzyMatching) {
            nameFilter = Filters.regex(Namable.FIELD_NAME_NAME, getPattern(word));
        } else {
            nameFilter = eq(Namable.FIELD_NAME_NAME, word);
        }

        if (!matchExchange) {
            return nameFilter;
        }

        final Bson exchangeFilter;
        if (fuzzyMatching) {
            exchangeFilter = Filters.regex("exchanges.word", getPattern(word));
        } else {
            exchangeFilter = eq("exchanges.word", word);
        }
        return Filters.or(nameFilter, exchangeFilter);
    }

    @Override
    public <E1 extends Entity, S1 extends Service<E1>> MongoIterable<WordWithJoin> findWithJoin(
            final Bson filter, final S1 joinService, final String joinField, final ObjectId joinId) {
        final Bson match = Aggregates.match(Filters.and(Filters.eq(joinField, joinId), MATCH_WORD_ID));
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .project(PROJECTION_WORD)
                .join(joinService, LET_WORD_ID, Arrays.asList(match, PROJECTION_JOIN_WORD))
                .build()
                .joinOne(DEFAULT_CONTEXT)
                .map(t -> {
                    final WordWithJoin wordWithJoin = EntityFactory.newInstance(WordWithJoin.class,
                            ((PojoFields) t.getKey()).fields());
                    wordWithJoin.setJoin(t.getValue().containsKey(joinService.getEntityClass()));
                    return wordWithJoin;
                });
    }

    private Pattern getPattern(final String word) {
        return Pattern.compile("^" + word + "$", Pattern.CASE_INSENSITIVE);
    }

    private Validation<Word> voiceSaverValidation(final ObjectId newWordId) {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final Word entity, final Word original, final Validate validate) {
                final Set<Phonetic> phonetics = entity.getPhonetics();
                if (!CollectionUtils.isEmpty(phonetics)) {
                    final ObjectId wordId = entity.getId() == null ? newWordId : entity.getId();
                    phonetics.forEach(phonetic -> validate(wordId, phonetic, validate));
                }
            }

            private void validate(final ObjectId wordId, final Phonetic phonetic, final Validate validate) {
                if (phonetic.getVoiceId() == null && phonetic.mp3() != null) {
                    if (wordId == null) {
                        validate.addFieldError(Entity.FIELD_NAME_ID, "保存音标语音时，wordId必须外部设置");
                    } else if (!validate.hasErrors()) {
                        saveVoice(wordId, phonetic, validate);
                    }
                }
            }

            private void saveVoice(final ObjectId wordId, final Phonetic phonetic, final Validate validate) {
                final Voice voice = EntityFactory.newInstance(Voice.class);
                voice.setContent(phonetic.mp3());
                voice.setContentType("audio/mpeg");
                voice.setSources(Collections.singletonMap("wordId", wordId));
                if (null != voiceService.save(voice, validate)) {
                    phonetic.setVoiceId(voice.getId());
                }
            }
        };
    }

    @Table
    public interface WordWithJoin extends Word {
        /**
         * 获得是否存在join.
         *
         * @return 是否存在join
         */
        boolean isJoin();

        /**
         * 设置是否存在join.
         *
         * @param join 是否存在join
         */
        void setJoin(boolean join);
    }
}
