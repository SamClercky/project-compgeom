package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SlidingIteratorTest {

    @Test
    void testIterator() {
        final var square = new Polygon(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(1, 0));
                add(new PVector(1, 1));
                add(new PVector(0, 1));
            }
        });

        final var iter = new SlidingIterator<>(3, square.ccwIterator());

        assertTrue(iter.hasNext());
        assertEquals(new ArrayList<>(Arrays.asList(
                new PVector(0, 0),
                new PVector(0, 1),
                new PVector(1, 1)
        )), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new ArrayList<>(Arrays.asList(
                new PVector(0, 1),
                new PVector(1, 1),
                new PVector(1, 0)
        )), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new ArrayList<>(Arrays.asList(
                new PVector(1, 1),
                new PVector(1, 0),
                new PVector(0, 0)
        )), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new ArrayList<>(Arrays.asList(
                new PVector(1, 0),
                new PVector(0, 0),
                new PVector(0, 1)
        )), iter.next());
        assertFalse(iter.hasNext());
        assertNull(iter.next());
    }

}