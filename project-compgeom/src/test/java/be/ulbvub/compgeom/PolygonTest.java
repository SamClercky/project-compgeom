package be.ulbvub.compgeom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolygonTest {
    Polygon square;
    Polygon triangle;
    Polygon rotatedSquare;

    @BeforeEach
    void setUp() {
        square = new Polygon(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(1, 0));
                add(new PVector(1, 1));
                add(new PVector(0, 1));
            }
        });
        triangle = new Polygon(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(0.5f, 1));
                add(new PVector(1, 0));
            }
        });
        rotatedSquare = new Polygon(new ArrayList<>() {
            {
                add(new PVector(1, 0.5f));
                add(new PVector(0.5f, 0));
                add(new PVector(0, 0.5f));
                add(new PVector(0.5f, 1));
            }
        });
    }

    @Test
    void ccwIterator() {
        final var squareIter = square.ccwIterator();
        assert squareIter != null; // should never happen
        assertEquals(new PVector(0, 0), squareIter.next());
        assertEquals(new PVector(0, 1), squareIter.next());
        assertEquals(new PVector(1, 1), squareIter.next());
        assertEquals(new PVector(1, 0), squareIter.next());

        final var triangleIter = triangle.ccwIterator();
        assert triangleIter != null;
        assertEquals(new PVector(0, 0), triangleIter.next());
        assertEquals(new PVector(0.5f, 1), triangleIter.next());
        assertEquals(new PVector(1, 0), triangleIter.next());

        final var rotatedSquareIter = rotatedSquare.ccwIterator();
        assert rotatedSquareIter != null;
        assertEquals(new PVector(0, 0.5f), rotatedSquareIter.next());
        assertEquals(new PVector(0.5f, 1), rotatedSquareIter.next());
        assertEquals(new PVector(1, 0.5f), rotatedSquareIter.next());
        assertEquals(new PVector(0.5f, 0), rotatedSquareIter.next());
    }
}