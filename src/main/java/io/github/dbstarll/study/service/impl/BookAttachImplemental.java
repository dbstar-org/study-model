package io.github.dbstarll.study.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.entity.Entity;
import io.github.dbstarll.dubai.model.service.Aggregator;
import io.github.dbstarll.dubai.model.service.Service;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.StudyEntities;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.service.StudyServices;
import io.github.dbstarll.study.service.attach.BookAttach;
import io.github.dbstarll.utils.lang.wrapper.EntryWrapper;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Map.Entry;

import static com.mongodb.client.model.Filters.eq;

public final class BookAttachImplemental<E extends StudyEntities & BookBase, S extends StudyServices<E>>
        extends StudyImplementals<E, S> implements BookAttach<E> {
    /**
     * 构建BookAttachImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public BookAttachImplemental(final S service, final Collection<E> collection) {
        super(service, collection);
    }

    @Override
    public Bson filterByBookId(final ObjectId bookId) {
        return eq(BookBase.FIELD_NAME_BOOK_ID, bookId);
    }

    @Override
    public long countByBookId(final ObjectId bookId) {
        return service.count(filterByBookId(bookId));
    }

    @Override
    public FindIterable<E> findByBookId(final ObjectId bookId) {
        return service.find(filterByBookId(bookId));
    }

    @Override
    public DeleteResult deleteByBookId(final ObjectId bookId) {
        return getCollection().deleteMany(filterByBookId(bookId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E1 extends Entity, S1 extends Service<E1>> MongoIterable<Entry<E, E1>> findWithBook(
            final S1 bookService, final Bson filter) {
        return Aggregator.builder(service, getCollection())
                .match(aggregateMatchFilter(filter))
                .join(bookService, BookBase.FIELD_NAME_BOOK_ID)
                .build()
                .aggregateOne(DEFAULT_CONTEXT)
                .map(e -> EntryWrapper.wrap(e.getKey(), (E1) e.getValue().get(bookService.getEntityClass())));
    }

    /**
     * 单词本/课本校验.
     *
     * @return finalBookIdValidation
     */
    @GeneralValidation
    public Validation<E> finalBookIdValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final E entity, final E original, final Validate validate) {
                if (entity.getBookId() == null) {
                    validate.addFieldError(BookBase.FIELD_NAME_BOOK_ID, "单词本/课本未设置");
                } else if (original != null && !entity.getBookId().equals(original.getBookId())) {
                    validate.addFieldError(BookBase.FIELD_NAME_BOOK_ID, "单词本/课本不可更改");
                }
            }
        };
    }
}
