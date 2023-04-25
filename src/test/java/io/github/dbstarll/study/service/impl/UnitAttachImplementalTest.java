package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.Book;
import io.github.dbstarll.study.entity.TestUnitEntity;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.service.BookService;
import io.github.dbstarll.study.service.TestUnitService;
import io.github.dbstarll.study.service.UnitService;
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

class UnitAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestUnitEntity> entityClass = TestUnitEntity.class;
    private static final Class<TestUnitService> serviceClass = TestUnitService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<Unit, TestUnitService> consumer) {
        useService(BookService.class, bookService -> {
            final Book book = EntityFactory.newInstance(Book.class);
            book.setName("课本");
            assertSame(book, bookService.save(book, null));

            useService(UnitService.class, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I i) throws AutowireException {
                    if (i instanceof BookAttachImplemental) {
                        ((BookAttachImplemental<?, ?>) i).setBookService(bookService);
                    }
                }
            }, unitService -> {
                final Unit unit = EntityFactory.newInstance(Unit.class);
                unit.setBookId(book.getId());
                assertSame(unit, unitService.save(unit, null));

                useService(serviceClass, new ImplementalAutowirer() {
                    @Override
                    public <I extends Implemental> void autowire(I i) throws AutowireException {
                        if (i instanceof UnitAttachImplemental) {
                            ((UnitAttachImplemental<?, ?>) i).setUnitService(unitService);
                        }
                    }
                }, s -> consumer.accept(unit, s));
            });
        });
    }

    @Test
    void filterByUnitId() {
        useServiceAutowirer((u, s) -> assertEquals(Filters.eq(UnitBase.FIELD_NAME_UNIT_ID, u.getId()),
                s.filterByUnitId(u.getId())));
    }

    @Test
    void countByUnitId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.countByUnitId(u.getId()));
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.countByUnitId(u.getId()));
        });
    }

    @Test
    void findByUnitId() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findByUnitId(new ObjectId()).first());
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(entity, s.findByUnitId(u.getId()).first());
        });
    }

    @Test
    void deleteByUnitId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.deleteByUnitId(u.getId()).getDeletedCount());
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.deleteByUnitId(u.getId()).getDeletedCount());
            assertNull(s.findById(entity.getId()));
        });
    }

    @Test
    void findWithUnit() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findWithUnit(null).first());

            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Entry<TestUnitEntity, Unit> match = s.findWithUnit(Filters.eq(entity.getId())).first();
            assertNotNull(match);
            assertEquals(entity, match.getKey());
            assertEquals(u, match.getValue());
        });
    }

    @Test
    void unitIdValidationNotSet() {
        useServiceAutowirer((u, s) -> {
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单元未设置"), validate.getFieldErrors()
                    .get(UnitBase.FIELD_NAME_UNIT_ID));
        });
    }

    @Test
    void unitIdValidationChange() {
        useServiceAutowirer((u, s) -> {
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertFalse(validate.hasErrors());

            entity.setUnitId(new ObjectId());
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单元不可更改"), validate.getFieldErrors()
                    .get(UnitBase.FIELD_NAME_UNIT_ID));
        });
    }

    @Test
    void unitIdValidationNotExist() {
        useServiceAutowirer((u, s) -> {
            final TestUnitEntity entity = EntityFactory.newInstance(entityClass);
            entity.setUnitId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("单元不存在"), validate.getFieldErrors()
                    .get(UnitBase.FIELD_NAME_UNIT_ID));
        });
    }
}