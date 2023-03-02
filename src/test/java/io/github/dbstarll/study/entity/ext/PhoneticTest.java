package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.study.entity.enums.PhoneticKey;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneticTest {
    @Test
    void testEquals() {
        final ObjectId voiceId = new ObjectId();
        final Phonetic phonetic = new Phonetic(PhoneticKey.AM, "am");
        phonetic.setVoiceId(voiceId);
        final Phonetic phonetic2 = new Phonetic();
        phonetic2.setKey(PhoneticKey.AM);
        phonetic2.setSymbol("am");
        phonetic2.setVoiceId(voiceId);

        assertTrue(Stream.of(null, "null").noneMatch(phonetic::equals));
        assertTrue(Stream.of(phonetic, phonetic2).allMatch(phonetic::equals));
    }

    @Test
    void testHashCode() {
        final Phonetic phonetic = new Phonetic(PhoneticKey.AM, "am");
        final Phonetic phonetic2 = new Phonetic();
        phonetic2.setKey(PhoneticKey.AM);
        phonetic2.setSymbol("am");

        assertEquals(phonetic.hashCode(), phonetic2.hashCode());
    }

    @Test
    void testToString() {
        final byte[] mp3 = "mp3".getBytes(StandardCharsets.UTF_8);
        final Phonetic phonetic = new Phonetic(PhoneticKey.AM, "am");
        final Phonetic phonetic2 = new Phonetic();
        phonetic2.setKey(PhoneticKey.AM);
        phonetic2.setSymbol("am");

        assertEquals("Phonetic[key=am, symbol='am', voiceId=null, mp3=0]", phonetic.toString());
        assertEquals(phonetic.toString(), phonetic2.toString());

        phonetic.mp3(mp3);
        phonetic2.mp3(mp3);
        assertEquals("Phonetic[key=am, symbol='am', voiceId=null, mp3=3]", phonetic.toString());
        assertEquals(phonetic.toString(), phonetic2.toString());
    }
}