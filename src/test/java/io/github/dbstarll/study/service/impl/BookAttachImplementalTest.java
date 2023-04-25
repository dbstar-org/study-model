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
import io.github.dbstarll.study.entity.TestBookEntity;
import io.github.dbstarll.study.entity.enums.SchoolLevel;
import io.github.dbstarll.study.entity.enums.Term;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.service.BookService;
import io.github.dbstarll.study.service.TestBookService;
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

class BookAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestBookEntity> entityClass = TestBookEntity.class;
    private static final Class<TestBookService> serviceClass = TestBookService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<Book, TestBookService> consumer) {
        useService(BookService.class, bookService -> {
            final Book book = EntityFactory.newInstance(Book.class);
            book.setName("课本");
            book.setSchool(SchoolLevel.B_MIDDLE);
            book.setTerm(Term.FIRST);
            assertSame(book, bookService.save(book, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I i) throws AutowireException {
                    if (i instanceof BookAttachImplemental) {
                        ((BookAttachImplemental<?, ?>) i).setBookService(bookService);
                    }
                }
            }, s -> consumer.accept(book, s));
        });
    }


    @Test
    void filterByBookId() {
        useServiceAutowirer((u, s) -> assertEquals(Filters.eq(BookBase.FIELD_NAME_BOOK_ID, u.getId()),
                s.filterByBookId(u.getId())));
    }

    @Test
    void countByBookId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.countByBookId(u.getId()));
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.countByBookId(u.getId()));
        });
    }

    @Test
    void findByBookId() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findByBookId(new ObjectId()).first());
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(entity, s.findByBookId(u.getId()).first());
        });
    }

    @Test
    void deleteByBookId() {
        useServiceAutowirer((u, s) -> {
            assertEquals(0, s.deleteByBookId(u.getId()).getDeletedCount());
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.deleteByBookId(u.getId()).getDeletedCount());
            assertNull(s.findById(entity.getId()));
        });
    }

    @Test
    void findWithBook() {
        useServiceAutowirer((u, s) -> {
            assertNull(s.findWithBook(null).first());

            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Entry<TestBookEntity, Book> match = s.findWithBook(Filters.eq(entity.getId())).first();
            assertNotNull(match);
            assertEquals(entity, match.getKey());
            assertEquals(u, match.getValue());
        });
    }

    @Test
    void bookIdValidationNotSet() {
        useServiceAutowirer((u, s) -> {
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("课本未设置"), validate.getFieldErrors()
                    .get(BookBase.FIELD_NAME_BOOK_ID));
        });
    }

    @Test
    void bookIdValidationChange() {
        useServiceAutowirer((u, s) -> {
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(u.getId());
            assertSame(entity, s.save(entity, null));

            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertFalse(validate.hasErrors());

            entity.setBookId(new ObjectId());
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("课本不可更改"), validate.getFieldErrors()
                    .get(BookBase.FIELD_NAME_BOOK_ID));
        });
    }

    @Test
    void bookIdValidationNotExist() {
        useServiceAutowirer((u, s) -> {
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(entity, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singletonList("课本不存在"), validate.getFieldErrors()
                    .get(BookBase.FIELD_NAME_BOOK_ID));
        });
    }
}
