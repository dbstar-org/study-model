package io.github.dbstarll.study.service.impl;

import com.mongodb.client.MongoIterable;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.info.Sourceable;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.Principal;
import io.github.dbstarll.study.entity.enums.Module;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.PrincipalService;
import io.github.dbstarll.study.service.attach.PrincipalServiceAttach;
import io.github.dbstarll.utils.lang.enums.EnumUtils;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.conversions.Bson;

import java.util.Map.Entry;

import static org.apache.commons.lang3.Validate.notNull;

public final class PrincipalServiceImplemental extends StudyImplementals<Principal, PrincipalService>
        implements PrincipalServiceAttach {
    private ExerciseBookService exerciseBookService;

    /**
     * 构造PrincipalServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public PrincipalServiceImplemental(final PrincipalService service, final Collection<Principal> collection) {
        super(service, collection);
    }

    /**
     * 设置ExerciseBookService.
     *
     * @param exerciseBookService ExerciseBookService
     */
    public void setExerciseBookService(final ExerciseBookService exerciseBookService) {
        this.exerciseBookService = exerciseBookService;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(exerciseBookService, "exerciseBookService is null");
    }

    @Override
    public MongoIterable<Entry<Principal, ExerciseBook>> findWithExerciseBook(final Bson filter) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .join(exerciseBookService, Sourceable.FIELD_NAME_SOURCES + '.' + EnumUtils.name(Module.ENGLISH))
                .build()
                .aggregateOne(DEFAULT_CONTEXT)
                .map(e -> EntryWrapper.wrap(e.getKey(), (ExerciseBook) e.getValue().get(ExerciseBook.class)));
    }
}
