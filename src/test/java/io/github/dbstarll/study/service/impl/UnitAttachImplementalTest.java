package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.study.entity.TestUnitEntity;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.service.TestUnitService;
import io.github.dbstarll.study.service.UnitService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class UnitAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestUnitEntity> entityClass = TestUnitEntity.class;
    private static final Class<TestUnitService> serviceClass = TestUnitService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<Unit, TestUnitService> consumer) {
        useService(UnitService.class, us -> {
            final Unit unit = EntityFactory.newInstance(Unit.class);
            assertSame(unit, us.save(unit, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I i) throws AutowireException {
                    if (i instanceof UnitAttachImplemental) {
                        ((UnitAttachImplemental<?, ?>) i).setUnitService(us);
                    }
                }
            }, s -> consumer.accept(unit, s));
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
}