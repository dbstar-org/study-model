package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.TestExerciseBookEntity;
import io.github.dbstarll.study.entity.join.ExerciseBookBase;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.TestExerciseBookService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseBookAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestExerciseBookEntity> entityClass = TestExerciseBookEntity.class;
    private static final Class<TestExerciseBookService> serviceClass = TestExerciseBookService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<ExerciseBook, TestExerciseBookService> consumer) {
        useService(ExerciseBookService.class, exerciseBookService -> {
            final ExerciseBook exerciseBook = EntityFactory.newInstance(ExerciseBook.class);
            exerciseBook.setName("练习册");
            assertSame(exerciseBook, exerciseBookService.save(exerciseBook, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I i) throws AutowireException {
                    if (i instanceof ExerciseBookAttachImplemental) {
                        ((ExerciseBookAttachImplemental<?, ?>) i).setExerciseBookService(exerciseBookService);
                    }
                }
            }, s -> consumer.accept(exerciseBook, s));
        });
    }


    @Test
    void filterByExerciseBookId() {
        useServiceAutowirer((u, s) -> assertEquals(Filters.eq(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID, u.getId()),
                s.filterByExerciseBookId(u.getId())));
    }

    @Test
    void countByExerciseBookId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.countByExerciseBookId(u.getId()));
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.countByExerciseBookId(u.getId()));
        });
    }

    @Test
    void findByExerciseBookId() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findByExerciseBookId(new ObjectId()).first());
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(entity, s.findByExerciseBookId(u.getId()).first());
        });
    }

    @Test
    void deleteByExerciseBookId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.deleteByExerciseBookId(u.getId()).getDeletedCount());
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.deleteByExerciseBookId(u.getId()).getDeletedCount());
            assertNull(s.findById(entity.getId()));
        });
    }

    @Test
    void findWithExerciseBook() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findWithExerciseBook(null).first());

            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Entry<TestExerciseBookEntity, ExerciseBook> match = s.findWithExerciseBook(Filters.eq(entity.getId())).first();
            assertNotNull(match);
            assertEquals(entity, match.getKey());
            assertEquals(u, match.getValue());
        });
    }

    @Test
    void exerciseBookIdValidationNotSet() {
        useServiceAutowirer((u, s) -> {
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("练习册未设置"), validate.getFieldErrors()
                    .get(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID));
        });
    }

    @Test
    void exerciseBookIdValidationChange() {
        useServiceAutowirer((u, s) -> {
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertFalse(validate.hasErrors());

            entity.setBookId(new ObjectId());
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("练习册不可更改"), validate.getFieldErrors()
                    .get(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID));
        });
    }

    @Test
    void exerciseBookIdValidationNotExist() {
        useServiceAutowirer((u, s) -> {
            final TestExerciseBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("练习册不存在"), validate.getFieldErrors()
                    .get(ExerciseBookBase.FIELD_NAME_EXERCISE_BOOK_ID));
        });
    }
}
