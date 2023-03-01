package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.TestWordEntity;
import io.github.dbstarll.study.entity.Word;
import io.github.dbstarll.study.entity.join.WordBase;
import io.github.dbstarll.study.service.TestWordService;
import io.github.dbstarll.study.service.VoiceService;
import io.github.dbstarll.study.service.WordService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestWordEntity> entityClass = TestWordEntity.class;
    private static final Class<TestWordService> serviceClass = TestWordService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<Word, TestWordService> consumer) {
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
        }, wordService -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("word");
            word.setCri(true);
            assertSame(word, wordService.save(word, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I implemental) throws AutowireException {
                    if (implemental instanceof WordAttachImplemental) {
                        ((WordAttachImplemental<?, ?>) implemental).setWordService(wordService);
                    }
                }
            }, service -> consumer.accept(word, service));
        }));
    }

    @Test
    void filterByWordId() {
        useServiceAutowirer((word, service) -> assertEquals(Filters.eq(WordBase.FIELD_NAME_WORD_ID, word.getId()),
                service.filterByWordId(word.getId())));
    }

    @Test
    void countByWordId() {
        useServiceAutowirer((word, service) -> {
            assertEquals(0, service.countByWordId(word.getId()));
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));
            assertEquals(1, service.countByWordId(word.getId()));
        });
    }

    @Test
    void findByWordId() {
        useServiceAutowirer((word, service) -> {
            assertNull(service.findByWordId(new ObjectId()).first());
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));
            assertEquals(entity, service.findByWordId(word.getId()).first());
        });
    }

    @Test
    void deleteByWordId() {
        useServiceAutowirer((word, service) -> {
            assertEquals(0, service.deleteByWordId(word.getId()).getDeletedCount());
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));
            assertEquals(1, service.deleteByWordId(word.getId()).getDeletedCount());
            assertNull(service.findById(entity.getId()));
        });
    }

    @Test
    void findWithWord() {
        useServiceAutowirer((word, service) -> {
            assertNull(service.findWithWord(null).first());

            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));

            final Entry<TestWordEntity, Word> match = service.findWithWord(Filters.eq(entity.getId())).first();
            assertNotNull(match);
            assertEquals(entity, match.getKey());
            assertEquals(word, match.getValue());
        });
    }

    @Test
    void distinctWordId() {
        useServiceAutowirer((word, service) -> {
            assertNull(service.distinctWordId(null).first());

            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));

            final List<ObjectId> ids = service.distinctWordId(Filters.eq(entity.getId())).into(new ArrayList<>());
            assertEquals(1, ids.size());
            assertEquals(word.getId(), ids.get(0));
        });
    }

    @Test
    void wordIdValidationNotSet() {
        useServiceAutowirer((word, service) -> {
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            final Validate validate = new DefaultValidate();
            assertNull(service.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单词未设置"), validate.getFieldErrors()
                    .get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void wordIdValidationNotSetInWord() {
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
        }, wordService -> {
            final Word word = EntityFactory.newInstance(Word.class);
            word.setName("wordNoCri");
            final Validate validate = new DefaultValidate();
            assertNull(wordService.save(word, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单词未设置"), validate.getFieldErrors()
                    .get(WordBase.FIELD_NAME_WORD_ID));
        }));
    }

    @Test
    void wordIdValidationChange() {
        useServiceAutowirer((word, service) -> {
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(word.getId());
            assertSame(entity, service.save(entity, null));

            final Validate validate = new DefaultValidate();
            assertNull(service.save(entity, validate));
            assertFalse(validate.hasErrors());

            entity.setWordId(new ObjectId());
            assertNull(service.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单词不可更改"), validate.getFieldErrors()
                    .get(WordBase.FIELD_NAME_WORD_ID));
        });
    }

    @Test
    void wordIdValidationNotExist() {
        useServiceAutowirer((word, service) -> {
            final TestWordEntity entity = EntityFactory.newInstance(entityClass);
            entity.setWordId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(service.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单词不存在"), validate.getFieldErrors()
                    .get(WordBase.FIELD_NAME_WORD_ID));
        });
    }
}