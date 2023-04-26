package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.info.Contentable;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.TestWordEntity;
import io.github.dbstarll.study.entity.Voice;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.enums.ExchangeKey;
import io.github.dbstarll.study.entity.enums.PhoneticKey;
import io.github.dbstarll.study.entity.ext.Exchange;
import io.github.dbstarll.study.entity.ext.Phonetic;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.TestWordService;
import io.github.dbstarll.study.service.UnitWordService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordServiceImplementalTest extends ServiceTestCase {
    private static final Class<Word> entityClass = Word.class;
    private static final Class<WordService> serviceClass = WordService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<VoiceService, WordService> consumer) {
        useService(VoiceService.class, voiceService -> useService(serviceClass, wordService -> {
            return new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                    if (implemental instanceof WordServiceImplemental) {
                        ((WordServiceImplemental) implemental).setVoiceService(voiceService);
                    } else if (implemental instanceof WordAttachImplemental) {
                        ((WordAttachImplemental<?, ?>) implemental).setWordService(wordService);
                    }
                }
            };
        }, wordService -> consumer.accept(voiceService, wordService)));
    }

    @Test
    void save() {
        useServiceAutowirer((voiceService, wordService) -> {
            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));
        });
    }

    @Test
    void filterByWord() {
        useServiceAutowirer((voiceService, wordService) -> {
            assertEquals("Filter{fieldName='name', value=word}",
                    wordService.filterByWord("word", false, false).toString());
            assertEquals("Or Filter{filters=[Filter{fieldName='name', value=word}, Filter{fieldName='exchanges.word', value=word}]}",
                    wordService.filterByWord("word", true, false).toString());
            assertEquals("Filter{fieldName='name', value=^word$}",
                    wordService.filterByWord("word", false, true).toString());
            assertEquals("Or Filter{filters=[Filter{fieldName='name', value=^word$}, Filter{fieldName='exchanges.word', value=^word$}]}",
                    wordService.filterByWord("word", true, true).toString());

            final Word wordDo = EntityFactory.newInstance(entityClass);
            wordDo.setName("do");
            wordDo.setCri(true);
            wordDo.setExchanges(Collections.singleton(new Exchange(ExchangeKey.DONE, "done", null)));
            assertSame(wordDo, wordService.save(wordDo, null));

            final Word wordMake = EntityFactory.newInstance(entityClass);
            wordMake.setName("done");
            wordMake.setCri(true);
            wordMake.setExchanges(Collections.singleton(new Exchange(ExchangeKey.PAST, "did", null)));
            assertSame(wordMake, wordService.save(wordMake, null));

            final List<Word> list1 = wordService.find(wordService.filterByWord("do", false, false)).into(new ArrayList<>());
            assertEquals(1, list1.size());
            assertEquals(wordDo, list1.get(0));

            final List<Word> list2 = wordService.find(wordService.filterByWord("did", true, false)).into(new ArrayList<>());
            assertEquals(1, list2.size());
            assertEquals(wordMake, list2.get(0));

            final List<Word> list3 = wordService.find(wordService.filterByWord("don.*", false, true)).into(new ArrayList<>());
            assertEquals(1, list3.size());
            assertEquals(wordMake, list3.get(0));

            final List<Word> list4 = wordService.find(wordService.filterByWord("di.*", true, true)).into(new ArrayList<>());
            assertEquals(1, list4.size());
            assertEquals(wordMake, list4.get(0));
        });
    }

    @Test
    void findWithJoin() {
        useServiceAutowirer((voiceService, wordService) -> {
            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            useService(UnitWordService.class, uws -> {
                final ObjectId unitId = new ObjectId();
                wordService.findWithJoin(null, uws, UnitBase.FIELD_NAME_UNIT_ID, unitId)
                        .forEach(w -> assertFalse(w.isJoin()));
            });

            useService(TestWordService.class, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                    if (implemental instanceof WordAttachImplemental) {
                        ((WordAttachImplemental<?, ?>) implemental).setWordService(wordService);
                    }
                }
            }, s -> {
                final TestWordEntity testWord = EntityFactory.newInstance(TestWordEntity.class);
                testWord.setWordId(word.getId());
                assertSame(testWord, s.save(testWord, null));

                final Bson filter = wordService.filterByWord("word", false, false);
                wordService.findWithJoin(filter, s, Entity.FIELD_NAME_ID, testWord.getId())
                        .forEach(w -> assertTrue(w.isJoin()));
            });
        });
    }

    @Test
    void voiceSaverValidationOnNewNoId() {
        useServiceAutowirer((voiceService, wordService) -> {
            final byte[] mp3 = "mp3".getBytes(StandardCharsets.UTF_8);
            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            word.setPhonetics(Collections.singleton(new Phonetic(PhoneticKey.AM, "word").mp3(mp3)));
            final Validate validate = new DefaultValidate();
            assertNull(wordService.save(word, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("保存音标语音时，wordId必须外部设置"),
                    validate.getFieldErrors().get(Entity.FIELD_NAME_ID));
        });
    }

    @Test
    void voiceSaverValidationOnNewId() {
        useServiceAutowirer((voiceService, wordService) -> {
            final byte[] mp3 = "mp3".getBytes(StandardCharsets.UTF_8);
            final Phonetic phonetic = new Phonetic(PhoneticKey.AM, "word").mp3(mp3);

            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            word.setPhonetics(Collections.singleton(phonetic));
            final ObjectId newId = new ObjectId();
            assertSame(word, wordService.save(word, newId, null));
            assertEquals(newId, word.getId());

            assertSame(phonetic, word.getPhonetics().iterator().next());
            assertNotNull(phonetic.getVoiceId());

            final Voice voice = voiceService.findById(phonetic.getVoiceId());
            assertNotNull(voice);
            assertEquals(Collections.singletonMap(WordBase.FIELD_NAME_WORD_ID, word.getId()), voice.getSources());
            assertEquals("audio/mpeg", voice.getContentType());
            assertArrayEquals(mp3, voice.getContent());
        });
    }

    @Test
    void voiceSaverValidationOnUpdate() {
        useServiceAutowirer((voiceService, wordService) -> {
            final byte[] mp3 = "mp3".getBytes(StandardCharsets.UTF_8);
            final Phonetic phonetic = new Phonetic(PhoneticKey.AM, "word");

            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            word.setPhonetics(Collections.singleton(phonetic));
            assertSame(word, wordService.save(word, null));

            phonetic.mp3(mp3);
            assertSame(word, wordService.save(word, null));

            assertSame(phonetic, word.getPhonetics().iterator().next());
            assertNotNull(phonetic.getVoiceId());

            final Voice voice = voiceService.findById(phonetic.getVoiceId());
            assertNotNull(voice);
            assertEquals(Collections.singletonMap(WordBase.FIELD_NAME_WORD_ID, word.getId()), voice.getSources());
            assertEquals("audio/mpeg", voice.getContentType());
            assertArrayEquals(mp3, voice.getContent());

            assertNull(wordService.save(word, null));
        });
    }

    @Test
    void voiceSaverValidationNoContent() {
        useServiceAutowirer((voiceService, wordService) -> {
            final Phonetic p1 = new Phonetic(PhoneticKey.AM, "word").mp3(new byte[0]);
            final Phonetic p2 = new Phonetic(PhoneticKey.EN, "word").mp3(new byte[0]);

            final Word word = EntityFactory.newInstance(entityClass);
            word.setName("word");
            word.setCri(true);
            word.setPhonetics(new HashSet<>(Arrays.asList(p1, p2)));

            final Validate validate = new DefaultValidate();
            assertNull(wordService.save(word, new ObjectId(), validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("内容未设置"),
                    validate.getFieldErrors().get(Contentable.FIELD_NAME_CONTENT));
        });
    }
}