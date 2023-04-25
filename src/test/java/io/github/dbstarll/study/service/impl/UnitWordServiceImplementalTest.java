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
import io.github.dbstarll.study.entity.Book;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.UnitWord;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.BookService;
import io.github.dbstarll.study.service.UnitService;
import io.github.dbstarll.study.service.UnitWordService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    private void useServiceAutowirer(final ThreeConsumer<WordService, Unit, UnitWordService> consumer) {
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
        }, wordService -> useService(BookService.class, bookService -> {
            final Book book = EntityFactory.newInstance(Book.class);
            book.setName("课本");
            book.setPrefix("单元");
            assertSame(book, bookService.save(book, null));

            useService(UnitService.class, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                    if (implemental instanceof BookAttachImplemental) {
                        ((BookAttachImplemental<?, ?>) implemental).setBookService(bookService);
                    }
                }
            }, unitService -> {
                final Unit unit = EntityFactory.newInstance(Unit.class);
                unit.setBookId(book.getId());
                unit.setSn("一");
                unit.setTitle("How are you!");
                assertSame(unit, unitService.save(unit, null));

                useService(serviceClass, new ImplementalAutowirer() {
                    @Override
                    public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                        if (implemental instanceof WordAttachImplemental) {
                            ((WordAttachImplemental<?, ?>) implemental).setWordService(wordService);
                        } else if (implemental instanceof BookAttachImplemental) {
                            ((BookAttachImplemental<?, ?>) implemental).setBookService(bookService);
                        } else if (implemental instanceof UnitAttachImplemental) {
                            ((UnitAttachImplemental<?, ?>) implemental).setUnitService(unitService);
                        } else if (implemental instanceof UnitWordServiceImplemental) {
                            ((UnitWordServiceImplemental) implemental).setWordService(wordService);
                            ((UnitWordServiceImplemental) implemental).setUnitService(unitService);
                        }
                    }
                }, service -> consumer.accept(wordService, unit, service));
            });
        })));
    }

    @Test
    void save() {
        useServiceAutowirer((wordService, unit, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("word");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final UnitWord entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(unit.getId());
            entity.setWordId(word.getId());
            assertSame(entity, s.save(entity, null));
        });
    }

    @Test
    void duplicateValidation() {
        useServiceAutowirer((wordService, unit, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setWordId(word.getId());
            unitWord.setUnitId(unit.getId());
            assertSame(unitWord, s.save(unitWord, null));

            final UnitWord anotherUnitWord = EntityFactory.newInstance(entityClass);
            anotherUnitWord.setWordId(word.getId());
            anotherUnitWord.setUnitId(unit.getId());
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
        useServiceAutowirer((wordService, unit, s) -> {
            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setUnitId(unit.getId());

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
        useServiceAutowirer((wordService, unit, s) -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("do");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            final UnitWord unitWord = EntityFactory.newInstance(entityClass);
            unitWord.setWordId(word.getId());
            unitWord.setUnitId(unit.getId());
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
}