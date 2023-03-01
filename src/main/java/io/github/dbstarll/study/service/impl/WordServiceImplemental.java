package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.Voice;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.ext.Phonetic;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.WordServiceAttach;
import io.github.dbstarll.utils.lang.wrapper.IterableWrapper;
import io.github.dbstarll.utils.lang.wrapper.IteratorWrapper;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
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
    private static final Bson PROJECTION_WORD = Aggregates.project(Projections.exclude("dateCreated", "lastModified"));
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
        return validateAndSave(entity, newEntityId, validate, new VoiceSaverValidation(newEntityId));
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
    public Iterable<WordWithJoin> findWithJoin(final Bson filter, final String joinTable, final String joinField,
                                               final ObjectId joinId,
                                               final Supplier<java.util.Collection<Bson>> query) {
        final List<Bson> pipeline = new LinkedList<>();
        if (filter != null) {
            pipeline.add(Aggregates.match(filter));
        }
        pipeline.addAll(query.get());
        pipeline.add(PROJECTION_WORD);
        pipeline.add(joinLookup(joinTable, joinField, joinId));
        return IterableWrapper.wrap(new IteratorWrapper<WordWithJoin, WordWithJoin>(
                getCollection().aggregate(pipeline, WordWithJoin.class).iterator()) {
            @Override
            protected WordWithJoin next(final WordWithJoin entity) {
                final List<Document> exercises = entity.getJoins();
                entity.setJoin(exercises != null && !exercises.isEmpty());
                entity.setJoins(null);
                return entity;
            }
        });
    }

    private Bson joinLookup(final String joinTable, final String joinField, final ObjectId joinId) {
        final Bson matchJoinId = Filters.eq(joinField, joinId);
        final Bson match = Aggregates.match(Filters.and(matchJoinId, MATCH_WORD_ID));
        return Aggregates.lookup(joinTable, LET_WORD_ID, Arrays.asList(match, PROJECTION_JOIN_WORD), "joins");
    }

    private Pattern getPattern(final String word) {
        return Pattern.compile("^" + word + "$", Pattern.CASE_INSENSITIVE);
    }

    private final class VoiceSaverValidation extends AbstractEntityValidation {
        private final ObjectId newWordId;

        private VoiceSaverValidation(final ObjectId newWordId) {
            this.newWordId = newWordId;
        }

        @Override
        public void validate(final Word entity, final Word original, final Validate validate) {
            final Set<Phonetic> phonetics = entity.getPhonetics();
            if (phonetics != null) {
                final ObjectId wordId = entity.getId() == null ? newWordId : entity.getId();

                for (Phonetic phonetic : phonetics) {
                    if (phonetic.getVoiceId() == null && phonetic.mp3() != null) {
                        if (wordId == null) {
                            validate.addFieldError(Entity.FIELD_NAME_ID, "保存音标语音时，wordId必须外部设置");
                        } else {
                            final Voice voice = EntityFactory.newInstance(Voice.class);
                            voice.setContent(phonetic.mp3());
                            voice.setContentType("audio/mpeg");
                            voice.setSources(Collections.singletonMap("wordId", wordId));
                            if (null != voiceService.save(voice, validate)) {
                                phonetic.setVoiceId(voice.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Table
    public interface WordWithJoin extends Word {
        /**
         * 获得外部join的文档列表.
         *
         * @return 外部join的文档列表
         */
        List<Document> getJoins();

        /**
         * 设置外部join的文档列表.
         *
         * @param joins 外部join的文档列表
         */
        void setJoins(List<Document> joins);

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
