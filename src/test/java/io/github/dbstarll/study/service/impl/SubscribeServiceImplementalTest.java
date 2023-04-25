package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.dubai.model.service.validate.DefaultValidate;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.study.entity.Subscribe;
import io.github.dbstarll.study.entity.enums.Module;
import io.github.dbstarll.study.entity.enums.Page;
import io.github.dbstarll.study.entity.enums.SubscribeType;
import io.github.dbstarll.study.service.SubscribeService;
import org.bson.types.ObjectId;
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

class SubscribeServiceImplementalTest extends ServiceTestCase {
    private static final Class<Subscribe> entityClass = Subscribe.class;
    private static final Class<SubscribeService> serviceClass = SubscribeService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    @Test
    void subscribeTypeNotSet() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("订阅类型未设置"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE));
        });
    }

    @Test
    void pageNotSet() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.PAGE);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(Subscribe.FIELD_NAME_PAGE), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("订阅的页面未设置"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_PAGE));
        });
    }

    @Test
    void subscribePageSetEntityId() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.PAGE);
            subscribe.setPage(Page.BOOK);
            subscribe.setEntityId(new ObjectId());
            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(Subscribe.FIELD_NAME_ENTITY_ID), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("PAGE类型的订阅不应设置订阅的实体ID"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_ENTITY_ID));
        });
    }

    @Test
    void subscribePage() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.PAGE);
            subscribe.setModule(Module.MATH);
            subscribe.setPage(Page.BOOK);
            assertSame(subscribe, s.save(subscribe, null));
            assertSame(Module.ENGLISH, subscribe.getModule());

            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, null));
            assertFalse(validate.hasErrors());

            subscribe.setType(SubscribeType.ENTITY);
            subscribe.setModule(Module.CHINESE);
            subscribe.setPage(Page.USER);
            subscribe.setEntityId(new ObjectId());
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE, Subscribe.FIELD_NAME_MODULE,
                    Subscribe.FIELD_NAME_PAGE, Subscribe.FIELD_NAME_ENTITY_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("订阅类型后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE));
            assertEquals(Collections.singletonList("订阅的模块设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_MODULE));
            assertEquals(Collections.singletonList("订阅的页面设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_PAGE));
            assertEquals(Collections.singletonList("订阅的实体ID设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_ENTITY_ID));
        });
    }

    @Test
    void entityIdNotSet() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.ENTITY);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Subscribe.FIELD_NAME_MODULE, Subscribe.FIELD_NAME_ENTITY_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("订阅的模块未设置"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_MODULE));
            assertEquals(Collections.singletonList("订阅的实体ID未设置"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_ENTITY_ID));
        });
    }

    @Test
    void subscribeEntitySetPage() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.ENTITY);
            subscribe.setModule(Module.ENGLISH);
            subscribe.setEntityId(new ObjectId());
            subscribe.setPage(Page.BOOK);
            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(Collections.singleton(Subscribe.FIELD_NAME_PAGE), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("ENTITY类型的订阅不应设置订阅的页面"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_PAGE));
        });
    }

    @Test
    void subscribeEntity() {
        useService(serviceClass, s -> {
            final Subscribe subscribe = EntityFactory.newInstance(entityClass);
            subscribe.setPrincipalId(new ObjectId());
            subscribe.setType(SubscribeType.ENTITY);
            subscribe.setModule(Module.ENGLISH);
            subscribe.setEntityId(new ObjectId());
            assertSame(subscribe, s.save(subscribe, null));

            final Validate validate = new DefaultValidate();
            assertNull(s.save(subscribe, null));
            assertFalse(validate.hasErrors());

            subscribe.setType(SubscribeType.PAGE);
            subscribe.setModule(Module.MATH);
            subscribe.setPage(Page.USER);
            subscribe.setEntityId(new ObjectId());
            assertNull(s.save(subscribe, validate));
            assertTrue(validate.hasErrors());
            assertTrue(validate.hasFieldErrors());
            assertEquals(new HashSet<>(Arrays.asList(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE, Subscribe.FIELD_NAME_MODULE,
                    Subscribe.FIELD_NAME_PAGE, Subscribe.FIELD_NAME_ENTITY_ID)), validate.getFieldErrors().keySet());
            assertEquals(Collections.singletonList("订阅类型后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE));
            assertEquals(Collections.singletonList("订阅的模块设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_MODULE));
            assertEquals(Collections.singletonList("订阅的页面设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_PAGE));
            assertEquals(Collections.singletonList("订阅的实体ID设置后不得修改"), validate.getFieldErrors().get(Subscribe.FIELD_NAME_ENTITY_ID));
        });
    }
}