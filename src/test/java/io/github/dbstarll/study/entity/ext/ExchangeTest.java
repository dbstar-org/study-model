package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.study.entity.enums.ExchangeKey;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeTest {
    @Test
    void testEquals() {
        final Exchange exchange = new Exchange(ExchangeKey.DONE, "done", null);
        final Exchange exchange2 = new Exchange();
        exchange2.setKey(ExchangeKey.DONE);
        exchange2.setWord("done");

        assertTrue(Stream.of(null, "null").noneMatch(exchange::equals));
        assertTrue(Stream.of(exchange, exchange2).allMatch(exchange::equals));
    }

    @Test
    void testHashCode() {
        final Exchange exchange = new Exchange(ExchangeKey.DONE, "done", null);
        final Exchange exchange2 = new Exchange();
        exchange2.setKey(ExchangeKey.DONE);
        exchange2.setWord("done");

        assertEquals(exchange.hashCode(), exchange2.hashCode());
    }

    @Test
    void testToString() {
        final Exchange exchange = new Exchange(ExchangeKey.DONE, "done", null);
        final Exchange exchange2 = new Exchange();
        exchange2.setKey(ExchangeKey.DONE);
        exchange2.setWord("done");

        assertEquals("Exchange[key=done, word='done', classify='null']", exchange.toString());
        assertEquals(exchange.toString(), exchange2.toString());
    }
}