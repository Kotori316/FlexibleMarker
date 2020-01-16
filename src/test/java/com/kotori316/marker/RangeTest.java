package com.kotori316.marker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeTest {

    @Test
    void validRange() {
        assertDoesNotThrow(() -> new Range(1, 5));
        assertThrows(IllegalArgumentException.class, () -> new Range(5, 1));
    }

    @Test
    void convert8Valid() {
        Range range = new Range(0, 8);
        assertAll(
            () -> assertEquals(4, range.convert(4), "Converting 4"),
            () -> assertEquals(0, range.convert(8), "Converting 8"),
            () -> assertEquals(0, range.convert(0), "Converting 0")
        );
    }

    @Test
    void convert8Plus() {
        Range range = new Range(0, 8);
        assertAll(
            () -> assertEquals(4, range.convert(12), "Converting 12"),
            () -> assertEquals(4, range.convert(12), "Converting 12"),
            () -> assertEquals(5, range.convert(13), "Converting 13"),
            () -> assertEquals(6, range.convert(14), "Converting 14"),
            () -> assertEquals(7, range.convert(15), "Converting 15"),
            () -> assertEquals(0, range.convert(16), "Converting 16"),
            () -> assertEquals(1, range.convert(17), "Converting 17"),
            () -> assertEquals(1.125f, range.convert(17.125f), "Converting 17.125f"),
            () -> assertEquals(0, range.convert(24), "Converting 24")
        );
    }

    @Test
    void convert8Minus() {
        Range range = new Range(0, 8);
        assertAll(
            () -> assertEquals(3, range.convert(-5), "Converting -5"),
            () -> assertEquals(4, range.convert(-4), "Converting -4"),
            () -> assertEquals(0, range.convert(-16), "Converting -16"),
            () -> assertEquals(7.875f, range.convert(-0.125f), "Converting -0.125f"),
            () -> assertEquals(6.875f, range.convert(-1.125f), "Converting -0.125f"),
            () -> assertEquals(0, range.convert(-8), "Converting -8")
        );
    }

    @Test
    void convert1_5() {
        Range range = new Range(1, 5);
        assertEquals(1, range.convert(1), "converting 1");
        assertEquals(1, range.convert(5), "converting 5");
        assertAll(
            () -> assertEquals(4.875f, range.convert(4.875f), "converting 4.875f"),
            () -> assertEquals(4, range.convert(0), "converting 0"),
            () -> assertEquals(3, range.convert(-1), "converting -1f"),
            () -> assertEquals(2, range.convert(-2), "converting -3"),
            () -> assertEquals(1, range.convert(-3), "converting -2"),
            () -> assertEquals(4, range.convert(-4), "converting -4"),
            () -> assertEquals(3 + 1, range.convert(8), "converting 8")
        );
    }
}
