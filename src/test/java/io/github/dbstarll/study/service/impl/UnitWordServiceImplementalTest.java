package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.dubai.model.service.BeanMap;
import io.github.dbstarll.dubai.model.service.Service;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.Book;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.UnitWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.BookService;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.ExerciseWordService;
import io.github.dbstarll.study.service.UnitService;
import io.github.dbstarll.study.service.UnitWordService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import io.github.dbstarll.study.service.attach.UnitWordServiceAttach.UnitWordWithExercise;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitWordServiceImplementalTest extends ServiceTestCase {
    private static final Class<UnitWord> entityClass = UnitWord.class;
    private static final Class<UnitWordService> serviceClass = UnitWordService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private <E extends Entity, S extends Service<E>> void nop(final S service) {
        // do nothing
    }

    private void useServiceAutowirer(final BiConsumer<BeanMap, UnitWordService> consumer) {
        useService(VoiceService.class, this::nop);
        useService(WordService.class, this::nop);
        useService(BookService.class, this::nop);
        useService(UnitService.class, this::nop);
        useService(ExerciseBookService.class, this::nop);
        useService(ExerciseWordService.class, this::nop);

        final BeanMap beanMap = new BeanMap();

        final Book book = EntityFactory.newInstance(Book.class);
        book.setName("课本");
        book.setPrefix("单元");
        assertSame(book, get(BookService.class).save(book, null));
        beanMap.put(Book.class, book);

        final Unit unit = EntityFactory.newInstance(Unit.class);
        unit.setBookId(book.getId());
        unit.setSn("一");
        unit.setTitle("How are you!");
        assertSame(unit, get(UnitService.class).save(unit, null));
        beanMap.put(Unit.class, unit);

        final Word word = EntityFactory.newInstance(Word.class);
        word.setName("do");
        word.setCri(true);
        assertSame(word, get(WordService.class).save(word, null));
        beanMap.put(Word.class, word);

        useService(serviceClass, service -> consumer.accept(beanMap, service));
    }

    @Test
    void save() {
        useServiceAutowirer((map, s) -> {
            final UnitWord entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(map.get(Unit.class).getId());
            entity.setWordId(map.get(Word.class).getId());
            assertSame(entity, s.save(entity, null));
        });
    }

    @Test
    void duplicateValidation() {
        useServiceAutowirer((map, s) -> {
            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setWordId(map.get(Word.class).getId());
            unitWord.setUnitId(map.get(Unit.class).getId());
            assertSame(unitWord, s.save(unitWord, null));

            final UnitWord anotherUnitWord = EntityFactory.newInstance(entityClass);
            anotherUnitWord.setWordId(map.get(Word.class).getId());
            anotherUnitWord.setUnitId(map.get(Unit.class).getId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(anotherUnitWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(WordBase.FIELD_NAME_WORD_ID), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("重复的单词"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationNoWordId() {
        useServiceAutowirer((map, s) -> {
            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setUnitId(map.get(Unit.class).getId());

            final Validate validate = new DefaultValidate();
            assertNull(s.save(unitWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Namable.FIELD_NAME_NAME, WordBase.FIELD_NAME_WORD_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("名称未设置"), validate.getFieldErrors().get(Namable.FIELD_NAME_NAME));
            assertEquals(Collections.singletonList("单词未设置"), validate.getFieldErrors().get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void packingValidationCanNotChanged() {
        useServiceAutowirer((map, s) -> {
            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setWordId(map.get(Word.class).getId());
            unitWord.setUnitId(map.get(Unit.class).getId());
            assertSame(unitWord, s.save(unitWord, null));
            assertEquals("do", unitWord.getName());

            assertNull(s.save(unitWord, null));

            unitWord.setName("other");
            final Validate validate = new DefaultValidate();
            assertNull(s.save(unitWord, validate));
            assertTrue(validate.hasErrors());
            assertFalse(validate.hasActionErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(Namable.FIELD_NAME_NAME), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("单词设置后不得修改"), validate.getFieldErrors().get(Namable.FIELD_NAME_NAME));
        });
    }

    @Test
    void findWithExercise() {
        useServiceAutowirer((map, s) -> {
            final Word word = map.get(Word.class);
            final Unit unit = map.get(Unit.class);

            final ExerciseBook exerciseBook = EntityFactory.newInstance(ExerciseBook.class);
            exerciseBook.setName("练习册");
            assertSame(exerciseBook, get(ExerciseBookService.class).save(exerciseBook, null));

            assertNull(s.findWithExercise(null, exerciseBook.getId()).first());

            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setWordId(word.getId());
            unitWord.setUnitId(unit.getId());
            assertSame(unitWord, s.save(unitWord, null));

            final ObjectId exerciseBookId = new ObjectId();

            final UnitWordWithExercise uwwe = s.findWithExercise(null, exerciseBook.getId()).first();
            assertNotNull(uwwe);
            assertNull(uwwe.getExercise());
            assertEquals(word, uwwe.getWord());


            final ExerciseWord exerciseWord = EntityFactory.newInstance(ExerciseWord.class);
            exerciseWord.setBookId(exerciseBook.getId());
            exerciseWord.setWordId(word.getId());
            assertSame(exerciseWord, get(ExerciseWordService.class).save(exerciseWord, null));

            final UnitWordWithExercise uwwe2 = s.findWithExercise(null, exerciseBook.getId()).first();
            assertNotNull(uwwe2);
            assertEquals(exerciseWord.getDateCreated(), uwwe2.getExercise().getDateCreated());
            assertNull(uwwe2.getWord());
        });
    }
}