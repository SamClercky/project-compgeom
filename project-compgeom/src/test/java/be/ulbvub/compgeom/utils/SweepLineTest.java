package be.ulbvub.compgeom.utils;

import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SweepLineTest {

    private static ArrayList<Line> createLinesAlong(PVector direction) {
        final var lines = new ArrayList<Line>();

        for (float i = 0; i < 10; i++) {
            var line = new Line(
                    direction.copy().mult(i * 2),
                    direction.copy().mult(i * 2).add(direction)
            );
            lines.add(line);
        }

        return lines;
    }

    private static void checkInCorrectOrder(ArrayList<Line> order, SweepLine sweepLine) {
        var iterator = sweepLine.descendingIterator();
        for (var line : order) {
            assertEquals(line, iterator.next());
        }
    }

    @Test
    void testXComparator() {
        var direction = new PVector(1, 0);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }

    @Test
    void testYComparator() {
        var direction = new PVector(0, 1);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }

    @Test
    void testGeneralComparator() {
        var direction = new PVector(1, 1);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }

    @Test
    void testGeneralComparator2() {
        var direction = new PVector(-1, 1);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }
}