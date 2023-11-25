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

    private static void checkInCorrectOrderReverse(ArrayList<Line> order, SweepLine sweepLine) {
        var iterator = sweepLine.descendingIterator();
        for (var i = order.size() - 1; i <= 0; i--) {
            assertEquals(order.get(i), iterator.next());
        }
    }

    @Test
    void testXComparatorStraight() {
        var direction = new PVector(1, 0);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }

    @Test
    void testXComparatorAbove() {
        var direction = new PVector(1, 0);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        for (var item : order) {
            item.end().add(new PVector(1, 1));
        }
        sweepline.addAll(order);
        checkInCorrectOrder(order, sweepline);
    }

    @Test
    void testXComparatorBelow() {
        var direction = new PVector(1, 0);
        var sweepline = SweepLine.fromDirection(direction);
        var order = createLinesAlong(direction);
        for (var item : order) {
            item.end().add(new PVector(1, -1));
        }
        sweepline.addAll(order);
        checkInCorrectOrderReverse(order, sweepline);
    }

    @Test
    void testXComparatorIndentHorizontal() {
        var sweepline = SweepLine.fromDirection(new PVector(1, 0));
        var higher = new Line(new PVector(343.0f, 448.58777f), new PVector(516.0f, 450.0f));
        var lower = new Line(new PVector(343.0f, 241.70114f), new PVector(530.0f, 246.0f));
        sweepline.add(higher);
        sweepline.add(lower);

        var reflexPoint = new Line(new PVector(413.0f, 328.0f), new PVector(414.0f, 328.0f));
        var comparator = new SweepLine.XComparator();

        assertEquals(-1, comparator.compare(lower, higher));
        assertEquals(1, comparator.compare(higher, lower));
        assertEquals(-1, comparator.compare(reflexPoint, higher));
        assertEquals(1, comparator.compare(higher, reflexPoint));
        assertEquals(1, comparator.compare(reflexPoint, lower));
        assertEquals(-1, comparator.compare(lower, reflexPoint));

        assertEquals(higher, sweepline.higher(reflexPoint));
        assertEquals(lower, sweepline.lower(reflexPoint));
    }

    @Test
    void testXComparatorIndentHorizontal45() {
        var sweepline = SweepLine.fromDirection(new PVector(0, 1));
        var higher = new Line(new PVector(73.53909f, 642.053f), new PVector(265.16504f, 437.6991f));
        var lower = new Line(new PVector(-67.88226f, 485.07526f), new PVector(89.80257f, 284.96405f));

        var reflexPoint = new Line(new PVector(81.317276f, 484.36816f), new PVector(81.317276f, 485.36816f));
        var comparator = new SweepLine.XComparator();

        assertEquals(-1, comparator.compare(lower, higher));
        assertEquals(1, comparator.compare(higher, lower));
        assertEquals(-1, comparator.compare(reflexPoint, higher));
        assertEquals(1, comparator.compare(higher, reflexPoint));
        assertEquals(1, comparator.compare(reflexPoint, lower));
        assertEquals(-1, comparator.compare(lower, reflexPoint));

        sweepline.add(higher);
        sweepline.add(lower);

        assertEquals(higher, sweepline.higher(reflexPoint));
        assertEquals(lower, sweepline.lower(reflexPoint));
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
        checkInCorrectOrderReverse(order, sweepline);
    }
}