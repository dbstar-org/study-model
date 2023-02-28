package io.github.dbstarll.study.service.impl;

import com.mongodb.client.model.Filters;
import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.study.entity.Book;
import io.github.dbstarll.study.entity.TestBookEntity;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.service.BookService;
import io.github.dbstarll.study.service.TestBookService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class BookAttachImplementalTest extends ServiceTestCase {
    private static final Class<TestBookEntity> entityClass = TestBookEntity.class;
    private static final Class<TestBookService> serviceClass = TestBookService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    @Test
    void filterByBookId() {
        final ObjectId bookId = new ObjectId();
        useService(serviceClass, s -> assertEquals(Filters.eq(BookBase.FIELD_NAME_BOOK_ID, bookId),
                s.filterByBookId(bookId)));
    }

    @Test
    void countByBookId() {
        final ObjectId bookId = new ObjectId();
        useService(serviceClass, s -> {
            assertEquals(0, s.countByBookId(bookId));
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(bookId);
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.countByBookId(bookId));
        });
    }

    @Test
    void findByBookId() {
        final ObjectId bookId = new ObjectId();
        useService(serviceClass, s -> {
            assertNull(s.findByBookId(bookId).first());
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(bookId);
            assertSame(entity, s.save(entity, null));
            assertEquals(entity, s.findByBookId(bookId).first());
        });
    }

    @Test
    void deleteByBookId() {
        final ObjectId bookId = new ObjectId();
        useService(serviceClass, s -> {
            assertEquals(0, s.deleteByBookId(bookId).getDeletedCount());
            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(bookId);
            assertSame(entity, s.save(entity, null));
            assertEquals(1, s.deleteByBookId(bookId).getDeletedCount());
            assertNull(s.findById(entity.getId()));
        });
    }

    @Test
    void findWithBook() {
        useService(BookService.class, bookService -> useService(serviceClass, s -> {
            assertNull(s.findWithBook(bookService, null).first());

            final Book book = EntityFactory.newInstance(Book.class);
            book.setName("测试");
            assertNotNull(bookService.save(book, null));

            final TestBookEntity entity = EntityFactory.newInstance(entityClass);
            entity.setBookId(new ObjectId());
            assertSame(entity, s.save(entity, null));

            final Entry<TestBookEntity, Book> match = s.findWithBook(bookService, Filters.eq(entity.getId())).first();
            assertNotNull(match);
            assertEquals(entity, match.getKey());
            assertNull(match.getValue());

            final TestBookEntity entity2 = EntityFactory.newInstance(entityClass);
            entity2.setBookId(book.getId());
            assertSame(entity2, s.save(entity2, null));

            final Entry<TestBookEntity, Book> match2 = s.findWithBook(bookService, Filters.eq(entity2.getId())).first();
            assertNotNull(match2);
            assertEquals(entity2, match2.getKey());
            assertEquals(book, match2.getValue());
        }));
    }
}