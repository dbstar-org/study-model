package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.utils.lang.enums.EnumUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class PageTest {
    @Test
    void allowMode() {
        final Map<Page, boolean[]> map = new HashMap<>();
        map.put(Page.BOOK, new boolean[]{false, true, false, false});
        map.put(Page.EXERCISE_BOOK, new boolean[]{false, true, true, false});
        map.put(Page.SPELL, new boolean[]{false, true, true, true});
        map.put(Page.SPELL_EXCHANGE, new boolean[]{false, true, true, false});
        map.put(Page.USER, new boolean[]{false, true, false, false});

        EnumUtils.stream(Page.class).forEach(page -> {
            final boolean[] allows = map.get(page);
            if (allows == null) {
                fail(String.format("test miss for page: %s", page));
            } else {
                final AtomicInteger index = new AtomicInteger();
                assertSame(allows[index.getAndIncrement()], page.allowMode(null),
                        String.format("page: %s, mode: null", page));
                EnumUtils.stream(Mode.class).forEach(m -> assertSame(allows[index.getAndIncrement()], page.allowMode(m),
                        String.format("page: %s, mode: %s", page, m)));
            }
        });
    }

    @Test
    void getModule() {
        final Map<Page, Module> map = new HashMap<>();
        map.put(Page.BOOK, Module.ENGLISH);
        map.put(Page.EXERCISE_BOOK, Module.ENGLISH);
        map.put(Page.SPELL, Module.ENGLISH);
        map.put(Page.SPELL_EXCHANGE, Module.ENGLISH);
        map.put(Page.USER, Module.SETTING);

        EnumUtils.stream(Page.class).forEach(page -> {
            final Module module = map.get(page);
            if (module == null) {
                fail(String.format("test miss for page: %s", page));
            } else {
                assertSame(module, page.getModule(), String.format("page: %s, module: %s", page, module));
            }
        });
    }
}