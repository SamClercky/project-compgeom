package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SlidingIteratorTest {

    @Test
    void testPolygonIterator() {
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

    @Test
    void testDCELIterator() {
        final var square = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(1, 0));
                add(new PVector(1, 1));
                add(new PVector(0, 1));
            }
        });

        final var iter = new SlidingIterator<>(3, square.getFaces().get(0).ccwIteratorVertex());

        assertTrue(iter.hasNext());
        assertVertices(
                new PVector(0, 0),
                new PVector(0, 1),
                new PVector(1, 1),
                iter.next());
        assertTrue(iter.hasNext());
        assertVertices(
                new PVector(0, 1),
                new PVector(1, 1),
                new PVector(1, 0),
                iter.next());
        assertTrue(iter.hasNext());
        assertVertices(
                new PVector(1, 1),
                new PVector(1, 0),
                new PVector(0, 0),
                iter.next());
        assertTrue(iter.hasNext());
        assertVertices(
                new PVector(1, 0),
                new PVector(0, 0),
                new PVector(0, 1),
                iter.next());
        assertFalse(iter.hasNext());
        assertNull(iter.next());
    }

    void assertVertices(PVector a, PVector b, PVector c, ArrayList<DCVertex> actual) {
        assertEquals(a, actual.get(0).getPoint());
        assertEquals(b, actual.get(1).getPoint());
        assertEquals(c, actual.get(2).getPoint());
    }
}