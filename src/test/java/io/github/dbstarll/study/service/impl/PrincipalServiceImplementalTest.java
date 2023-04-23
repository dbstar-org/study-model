package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.entity.EntityFactory;
import io.github.dbstarll.dubai.model.service.AutowireException;
import io.github.dbstarll.dubai.model.service.Implemental;
import io.github.dbstarll.dubai.model.service.ImplementalAutowirer;
import io.github.dbstarll.dubai.model.service.ServiceTestCase;
import io.github.dbstarll.study.entity.ExerciseBook;
import io.github.dbstarll.study.entity.Principal;
import io.github.dbstarll.study.entity.enums.Mode;
import io.github.dbstarll.study.entity.enums.Module;
import io.github.dbstarll.study.service.ExerciseBookService;
import io.github.dbstarll.study.service.PrincipalService;
import io.github.dbstarll.utils.lang.enums.EnumUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PrincipalServiceImplementalTest extends ServiceTestCase {
    private static final Class<Principal> entityClass = Principal.class;
    private static final Class<PrincipalService> serviceClass = PrincipalService.class;

    @BeforeAll
    static void beforeAll() {
        globalCollectionFactory();
    }

    private void useServiceAutowirer(final BiConsumer<ExerciseBook, PrincipalService> consumer) {
        useService(ExerciseBookService.class, exerciseBookService -> {
            final ExerciseBook exerciseBook = EntityFactory.newInstance(ExerciseBook.class);
            exerciseBook.setName("练习册");
            assertSame(exerciseBook, exerciseBookService.save(exerciseBook, null));

            useService(serviceClass, new ImplementalAutowirer() {
                @Override
                public <I extends Implemental> void autowire(I i) throws AutowireException {
                    if (i instanceof PrincipalServiceImplemental) {
                        ((PrincipalServiceImplemental) i).setExerciseBookService(exerciseBookService);
                    }
                }
            }, s -> consumer.accept(exerciseBook, s));
        });
    }

    @Test
    void findWithExerciseBook() {
        useServiceAutowirer((eb, s) -> {
            assertNull(s.findWithExerciseBook(null, Module.ENGLISH).first());

            final Principal principal = EntityFactory.newInstance(entityClass);
            principal.setMode(Mode.ADMIN);
            principal.setSources(Collections.singletonMap(EnumUtils.name(Module.ENGLISH), eb.getId()));
            assertSame(principal, s.save(principal, null));

            final List<Entry<Principal, ExerciseBook>> list = s.findWithExerciseBook(null, Module.ENGLISH).into(new ArrayList<>());
            assertEquals(1, list.size());
            assertEquals(principal, list.get(0).getKey());
            assertEquals(eb, list.get(0).getValue());

            final List<Entry<Principal, ExerciseBook>> list2 = s.findWithExerciseBook(null, Module.MATH).into(new ArrayList<>());
            assertEquals(1, list2.size());
            assertEquals(principal, list2.get(0).getKey());
            assertNull(list2.get(0).getValue());
        });
    }
}