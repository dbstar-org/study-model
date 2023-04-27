package io.github.dbstarll.study.classify.exchange;

import io.github.dbstarll.study.entity.enums.ExchangeKey;
import io.github.dbstarll.utils.lang.enums.EnumUtils;
import io.github.dbstarll.utils.lang.line.LineValidator;
import io.github.dbstarll.utils.lang.line.Lines;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ExchangeUtilsTest {
    @Test
    void classify() {
        for (String line : Lines.open("classify.txt", StandardCharsets.UTF_8, LineValidator.NOT_BLANK)) {
            final String[] part = StringUtils.split(line, '\t');
            final ExchangeKey key = EnumUtils.valueOf(ExchangeKey.class, part[0]);
            final String expected = part[1];
            final String classify = ExchangeUtils.classify(key, part[2], part[3]);
            if ("null".equals(expected)) {
                assertNull(classify, line);
            } else {
                assertEquals(expected, classify, line);
            }
        }
    }

    @Test
    void classifyNullKey() {
        final Exception e = assertThrowsExactly(NullPointerException.class,
                () -> ExchangeUtils.classify(null, "word", "words"));
        assertEquals("ExchangeKey not set", e.getMessage());
    }

    @Test
    void classifyEmptyWord() {
        final Exception e = assertThrowsExactly(IllegalArgumentException.class,
                () -> ExchangeUtils.classify(ExchangeKey.PL, "", "words"));
        assertEquals("word not set", e.getMessage());
    }

    @Test
    void classifyEmptyExchange() {
        final Exception e = assertThrowsExactly(IllegalArgumentException.class,
                () -> ExchangeUtils.classify(ExchangeKey.PL, "word", ""));
        assertEquals("exchange not set", e.getMessage());
    }
}