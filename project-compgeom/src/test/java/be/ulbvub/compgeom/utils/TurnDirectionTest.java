package be.ulbvub.compgeom.utils;

import org.junit.jupiter.api.Test;
import processing.core.PVector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnDirectionTest {

    @Test
    void orientation() {
        var p1 = new PVector(0, 0);
        var p2 = new PVector(1, 0);
        var p3 = new PVector(1, 1);
        var p4 = new PVector(1, -1);

        assertEquals(TurnDirection.LEFT, TurnDirection.orientation(p1, p2, p3));
        assertEquals(TurnDirection.RIGHT, TurnDirection.orientation(p1, p2, p4));
    }
}