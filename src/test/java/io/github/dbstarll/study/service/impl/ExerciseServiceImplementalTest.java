package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.ThreeConsumer;
import io.github.dbstarll.study.entity.Exercise;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.enums.ExchangeKey;
import io.github.dbstarll.study.entity.enums.ExerciseKey;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.ExerciseService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseServiceImplementalTest extends ServiceTestCase {
    private static final Class<Exercise> entityClass = Exercise.class;
    private static final Class<ExerciseService> serviceClass = ExerciseService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final ThreeConsumer<WordService, ExerciseBook, ExerciseService> consumer) {
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
                    } else if (implemental instanceof ExerciseServiceImplemental) {
                        ((ExerciseServiceImplemental) implemental).setWordService(wordService);
                    }
                }
            }, service -> consumer.accept(wordService, exerciseBook, service));
        })));
    }

    @Test
    void countErrors() {
        useServiceAutowirer((wordService, exerciseBook, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            s.save(exercise(word, exerciseBook, ExchangeKey.DONE, true, "done"), null);
            s.save(exercise(word, exerciseBook, ExchangeKey.DONE, false, "done1"), null);
            s.save(exercise(word, exerciseBook, ExchangeKey.DONE, false, "done2"), null);
            s.save(exercise(word, exerciseBook, ExchangeKey.DONE, false, "done2"), null);
            s.save(exercise(word, exerciseBook, null, false, "done3"), null);
            s.save(exercise(word, exerciseBook, null, false, "done3"), null);
            s.save(exercise(word, exerciseBook, null, false, "done1"), null);

            final List<Entry<String, Integer>> list1 = s
                    .countErrors(exercise(word, exerciseBook, ExchangeKey.DONE, false, "done"))
                    .into(new ArrayList<>());
            assertEquals("[done2=2, done1=1]", list1.toString());

            final List<Entry<String, Integer>> list2 = s
                    .countErrors(exercise(word, exerciseBook, null, false, "done"))
                    .into(new ArrayList<>());
            assertEquals("[done3=2, done1=1]", list2.toString());
        });
    }

    @Test
    void packingValidation() {
        useServiceAutowirer((wordService, exerciseBook, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final Exercise exercise = s.save(exercise(word, exerciseBook, ExchangeKey.DONE, true, "done"), null);

            assertNull(s.save(exercise, null));

            exercise.setDescription("done2");
            final Validate validate = new DefaultValidate();
            assertNull(s.save(exercise, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasActionErrors());
            assertFalse(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("练习结果不得修改"), validate.getActionErrors());
        });
    }

    private Exercise exercise(final Word word, final ExerciseBook exerciseBook, final ExchangeKey exchangeKey,
                              final boolean correct, final String spell) {
        final Exercise exercise = EntityFactory.newInstance(entityClass);
        exercise.setWordId(word.getId());
        exercise.setBookId(exerciseBook.getId());
        exercise.setExchangeKey(exchangeKey);
        exercise.setExerciseKey(ExerciseKey.SPELL);
        exercise.setCorrect(correct);
        exercise.setDescription(spell);
        return exercise;
    }
}