package io.github.dbstarll.study.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.Unit;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.service.StudyServices;
import io.github.dbstarll.study.service.UnitService;
import io.github.dbstarll.study.service.attach.UnitAttach;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.Validate.notNull;

public final class UnitAttachImplemental<E extends StudyEntities & UnitBase, S extends StudyServices<E>>
        extends StudyImplementals<E, S> implements UnitAttach<E> {
    private UnitService unitService;

    /**
     * 构造UnitAttachImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public UnitAttachImplemental(final S service, final Collection<E> collection) {
        super(service, collection);
    }

    /**
     * 设置UnitService.
     *
     * @param unitService UnitService实例
     */
    public void setUnitService(final UnitService unitService) {
        this.unitService = unitService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(unitService, "unitService not set.");
    }

    @Override
    public Bson filterByUnitId(final ObjectId unitId) {
        return eq(UnitBase.FIELD_NAME_UNIT_ID, unitId);
    }

    @Override
    public long countByUnitId(final ObjectId unitId) {
        return service.count(filterByUnitId(unitId));
    }

    @Override
    public FindIterable<E> findByUnitId(final ObjectId unitId) {
        return service.find(filterByUnitId(unitId));
    }

    @Override
    public DeleteResult deleteByUnitId(final ObjectId unitId) {
        return getCollection().deleteMany(filterByUnitId(unitId));
    }

    @Override
    public MongoIterable<Entry<E, Unit>> findWithUnit(final Bson filter) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .join(unitService, UnitBase.FIELD_NAME_UNIT_ID)
                .build()
                .aggregateOne(DEFAULT_CONTEXT)
                .map(e -> EntryWrapper.wrap(e.getKey(), (Unit) e.getValue().get(Unit.class)));
    }
}
