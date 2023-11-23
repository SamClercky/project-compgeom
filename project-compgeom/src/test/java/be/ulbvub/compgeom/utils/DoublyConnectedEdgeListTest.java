package be.ulbvub.compgeom.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DoublyConnectedEdgeListTest {
    DoublyConnectedEdgeList square;
    DoublyConnectedEdgeList triangle;
    DoublyConnectedEdgeList rotatedSquare;

    @BeforeEach
    void setUp() {
        square = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(1, 0));
                add(new PVector(1, 1));
                add(new PVector(0, 1));
            }
        });
        triangle = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(0.5f, 1));
                add(new PVector(1, 0));
            }
        });
        rotatedSquare = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(1, 0.5f));
                add(new PVector(0.5f, 0));
                add(new PVector(0, 0.5f));
                add(new PVector(0.5f, 1));
            }
        });
    }

    @Test
    void testNumberOfFacesVerticesEdges() {
        assertEquals(1, square.getFaces().size());
        assertEquals(4, square.getVertices().size());
        assertEquals(4 * 2, square.getEdges().size());

        assertEquals(1, triangle.getFaces().size());
        assertEquals(3, triangle.getVertices().size());
        assertEquals(3 * 2, triangle.getEdges().size());

        assertEquals(1, rotatedSquare.getFaces().size());
        assertEquals(4, rotatedSquare.getVertices().size());
        assertEquals(4 * 2, rotatedSquare.getEdges().size());
    }

    @Test
    void ccwEdgeIterator() {
        final var squareIter = square.getFaces().get(0).ccwIteratorEdge();
        assertEdge(new PVector(0, 0), squareIter.next());
        assertTrue(squareIter.hasNext());
        assertEdge(new PVector(0, 1), squareIter.next());
        assertTrue(squareIter.hasNext());
        assertEdge(new PVector(1, 1), squareIter.next());
        assertTrue(squareIter.hasNext());
        assertEdge(new PVector(1, 0), squareIter.next());
        assertFalse(squareIter.hasNext());

        final var triangleIter = triangle.getFaces().get(0).ccwIteratorEdge();
        assertEdge(new PVector(0, 0), triangleIter.next());
        assertTrue(triangleIter.hasNext());
        assertEdge(new PVector(0.5f, 1), triangleIter.next());
        assertTrue(triangleIter.hasNext());
        assertEdge(new PVector(1, 0), triangleIter.next());
        assertFalse(triangleIter.hasNext());

        final var rotatedSquareIter = rotatedSquare.getFaces().get(0).ccwIteratorEdge();
        assertEdge(new PVector(0, 0.5f), rotatedSquareIter.next());
        assertTrue(rotatedSquareIter.hasNext());
        assertEdge(new PVector(0.5f, 1), rotatedSquareIter.next());
        assertTrue(rotatedSquareIter.hasNext());
        assertEdge(new PVector(1, 0.5f), rotatedSquareIter.next());
        assertTrue(rotatedSquareIter.hasNext());
        assertEdge(new PVector(0.5f, 0), rotatedSquareIter.next());
        assertFalse(rotatedSquareIter.hasNext());
    }

    void assertEdge(PVector expectedOrigin, DCHalfEdge actual) {
        assertEquals(expectedOrigin, actual.getOrigin().getPoint());
    }
}