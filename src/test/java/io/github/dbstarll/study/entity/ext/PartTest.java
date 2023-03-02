package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.study.entity.enums.PartKey;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartTest {
    @Test
    void testEquals() {
        final Part part = new Part(Collections.singletonList(PartKey.ADJ), Collections.singletonList("adj"));
        final Part part2 = new Part();
        part2.setKey(Collections.singletonList(PartKey.ADJ));
        part2.setMeans(Collections.singletonList("adj"));

        assertTrue(Stream.of(null, "null").noneMatch(part::equals));
        assertTrue(Stream.of(part, part2).allMatch(part::equals));
    }

    @Test
    void testHashCode() {
        final Part part = new Part(Collections.singletonList(PartKey.ADJ), Collections.singletonList("adj"));
        final Part part2 = new Part();
        part2.setKey(Collections.singletonList(PartKey.ADJ));
        part2.setMeans(Collections.singletonList("adj"));

        assertEquals(part.hashCode(), part2.hashCode());
    }

    @Test
    void testToString() {
        final Part part = new Part(Collections.singletonList(PartKey.ADJ), Collections.singletonList("adj"));
        final Part part2 = new Part();
        part2.setKey(Collections.singletonList(PartKey.ADJ));
        part2.setMeans(Collections.singletonList("adj"));

        assertEquals("Part[key=[adj], means=[adj]]", part.toString());
        assertEquals(part.toString(), part2.toString());
    }
}