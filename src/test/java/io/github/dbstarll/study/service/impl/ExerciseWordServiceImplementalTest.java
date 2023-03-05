package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.ThreeConsumer;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.enums.ExchangeKey;
import io.github.dbstarll.study.entity.ext.Exchange;
import io.github.dbstarll.study.entity.ext.MasterPercent;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.ExerciseWordService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseWordServiceImplementalTest extends ServiceTestCase {
    private static final Class<ExerciseWord> entityClass = ExerciseWord.class;
    private static final Class<ExerciseWordService> serviceClass = ExerciseWordService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final ThreeConsumer<WordService, ExerciseBook, ExerciseWordService> consumer) {
        useService(VoiceService.class, voiceService -> useService(WordService.class, wordService -> {
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
        }, wordService -> useService(ExerciseBookService.class, exerciseBookService -> {
            final ExerciseBook exerciseBook = EntityFactory.newInstance(ExerciseBook.class);
            exerciseBook.setName("练习册");
            assertSame(exerciseBook, exerciseBookService.save(exerciseBook, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                    if (implemental instanceof WordAttachImplemental) {
                        ((WordAttachImplemental<?, ?>) implemental).setWordService(wordService);
                    } else if (implemental instanceof ExerciseBookAttachImplemental) {
                        ((ExerciseBookAttachImplemental<?, ?>) implemental).setExerciseBookService(exerciseBookService);
                    } else if (implemental instanceof ExerciseWordServiceImplemental) {
                        ((ExerciseWordServiceImplemental) implemental).setWordService(wordService);
                    }
                }
            }, service -> consumer.accept(wordService, exerciseBook, service));
        })));
    }

    @Test
    void sample() {
        useServiceAutowirer((wordService, b, s) -> {
            final Word word1 = EntityFactory.newInstance(Word.class);
            word1.setName("word1");
            word1.setCri(true);
            assertSame(word1, wordService.save(word1, null));

            final Word word2 = EntityFactory.newInstance(Word.class);
            word2.setName("word2");
            word2.setCri(true);
            assertSame(word2, wordService.save(word2, null));

            final ExerciseWord exerciseWord1 = EntityFactory.newInstance(entityClass);
            exerciseWord1.setWordId(word1.getId());
            exerciseWord1.setBookId(b.getId());
            assertSame(exerciseWord1, s.save(exerciseWord1, null));

            final ExerciseWord exerciseWord2 = EntityFactory.newInstance(entityClass);
            exerciseWord2.setWordId(word2.getId());
            exerciseWord2.setBookId(b.getId());
            assertSame(exerciseWord2, s.save(exerciseWord2, null));

            final AtomicInteger match1 = new AtomicInteger();
            final AtomicInteger match2 = new AtomicInteger();
            for (int i = 0; i < 10; i++) {
                final List<ExerciseWord> match = s.sample(s.filterByExerciseBookId(b.getId()), 1).into(new ArrayList<>());
                assertNotNull(match);
                assertEquals(1, match.size());
                if (exerciseWord1.equals(match.get(0))) {
                    match1.incrementAndGet();
                } else if (exerciseWord2.equals(match.get(0))) {
                    match2.incrementAndGet();
                }
            }
            assertTrue(match1.get() > 0);
            assertTrue(match2.get() > 0);
            assertEquals(10, match1.get() + match2.get());
        });
    }

    @Test
    void filterByInterfere() {
        useServiceAutowirer((wordService, b, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("word1");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setWordId(word.getId());
            exerciseWord.setBookId(b.getId());
            assertSame(exerciseWord, s.save(exerciseWord, null));

            assertEquals(String.format(
                    "And Filter{filters=[Filter{fieldName='bookId', value=%s}, Operator Filter{fieldName='_id', operator='$nin', value=[%s]}]}",
                    b.getId(), exerciseWord.getId()),
                    s.filterByInterfere(exerciseWord, null).toString());
            final Pattern pattern = Pattern.compile("^w.*$", Pattern.CASE_INSENSITIVE);
            assertEquals(String.format(
                    "And Filter{filters=[Filter{fieldName='bookId', value=%s}, Operator Filter{fieldName='_id', operator='$nin', value=[%s]}, Filter{fieldName='name', value=%s}]}",
                    b.getId(), exerciseWord.getId(), pattern),
                    s.filterByInterfere(exerciseWord, pattern).toString());
        });
    }

    @Test
    void duplicateValidation() {
        useServiceAutowirer((wordService, b, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setWordId(word.getId());
            exerciseWord.setBookId(b.getId());
            assertSame(exerciseWord, s.save(exerciseWord, null));

            final ExerciseWord anotherExerciseWord = EntityFactory.newInstance(entityClass);
            anotherExerciseWord.setWordId(word.getId());
            anotherExerciseWord.setBookId(b.getId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(anotherExerciseWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(WordBase.FIELD_NAME_WORD_ID), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("重复的单词"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationNoWordId() {
        useServiceAutowirer((wordService, b, s) -> {
            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setBookId(b.getId());

            final Validate validate = new DefaultValidate();
            assertNull(s.save(exerciseWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Namable.FIELD_NAME_NAME, WordBase.FIELD_NAME_WORD_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("名称未设置"), validate.getFieldErrors().get(Namable.FIELD_NAME_NAME));
            assertEquals(Collections.singletonList("单词未设置"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationWordNotExist() {
        useServiceAutowirer((wordService, b, s) -> {
            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setWordId(new ObjectId());
            exerciseWord.setBookId(b.getId());

            final Validate validate = new DefaultValidate();
            assertNull(s.save(exerciseWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Namable.FIELD_NAME_NAME, WordBase.FIELD_NAME_WORD_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("名称未设置"), validate.getFieldErrors().get(Namable.FIELD_NAME_NAME));
            assertEquals(Collections.singletonList("单词不存在"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationNotCri() {
        useServiceAutowirer((wordService, b, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final Word word2 = EntityFactory.newInstance(Word.class);
            word2.setName("done");
            word2.setWordId(word.getId());
            assertSame(word2, wordService.save(word2, null));

            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setWordId(word2.getId());
            exerciseWord.setBookId(b.getId());

            final Validate validate = new DefaultValidate();
            assertNull(s.save(exerciseWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(WordBase.FIELD_NAME_WORD_ID), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("这是一个派生词，请添加原型词"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationCanNotChanged() {
        useServiceAutowirer((wordService, b, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            word.setExchanges(new HashSet<>(Arrays.asList(
                    new Exchange(ExchangeKey.PAST, "done", "abc"),
                    new Exchange(ExchangeKey.PL, "di d", "abc"))));
            assertSame(word, wordService.save(word, null));

            final ExerciseWord exerciseWord = EntityFactory.newInstance(entityClass);
            exerciseWord.setWordId(word.getId());
            exerciseWord.setBookId(b.getId());
            assertSame(exerciseWord, s.save(exerciseWord, null));
            assertEquals("{past=Exchange[key=null, word='done', classify='abc']}", exerciseWord.getExchanges().toString());
            assertEquals("do", exerciseWord.getName());

            final MasterPercent masterPercent = new MasterPercent();
            masterPercent.setBingo(1);
            masterPercent.setCorrect(1);
            masterPercent.setTotal(1);
            masterPercent.setLast(new Date());
            masterPercent.setNext(new Date());
            masterPercent.setPercent(0.5f);
            exerciseWord.setMasterPercents(Collections.singletonMap("spell", masterPercent));
            assertSame(exerciseWord, s.save(exerciseWord, null));
            assertTrue(exerciseWord.getMasterPercents().toString().startsWith("{spell=MasterPercent [percent=0.5, correct=1/1/1, last="));

            final ExerciseWord loaded = s.findById(exerciseWord.getId());
            assertNotNull(loaded);
            assertTrue(Stream.of(null, "null").noneMatch(masterPercent::equals));
            assertTrue(Stream.of(masterPercent, loaded.getMasterPercents().get("spell")).allMatch(masterPercent::equals));
            assertEquals(masterPercent.hashCode(), loaded.getMasterPercents().get("spell").hashCode());

            exerciseWord.setName("other");
            exerciseWord.getExchanges().get("past").setClassify("dev");
            final Validate validate = new DefaultValidate();
            assertNull(s.save(exerciseWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Namable.FIELD_NAME_NAME, ExerciseWord.FIELD_NAME_EXCHANGES)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("单词设置后不得修改"), validate.getFieldErrors().get(Namable.FIELD_NAME_NAME));
            assertEquals(Collections.singletonList("单词设置后不得修改"), validate.getFieldErrors().get(ExerciseWord.FIELD_NAME_EXCHANGES));
        });
    }
}