package io.github.dbstarll.study.entity.enums;

import io.github.dbstarll.utils.lang.enums.EnumUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ExerciseKeyTest {
    @Test
    void maxLevel() {
        final Map<ExerciseKey, Integer> map = new HashMap<>();
        map.put(ExerciseKey.LISTEN, 3);
        map.put(ExerciseKey.SPELL, 14);
        map.put(ExerciseKey.READ, 0);
        map.put(ExerciseKey.WRITE, 0);
        EnumUtils.stream(ExerciseKey.class).forEach(key -> {
            final Integer level = map.get(key);
            if (level == null) {
                fail(String.format("test miss for ExerciseKey: %s", key));
            } else {
                assertEquals(level.intValue(), key.maxLevel(), String.format("ExerciseKey: %s, level: %s", key, level));
            }
        });
    }
}