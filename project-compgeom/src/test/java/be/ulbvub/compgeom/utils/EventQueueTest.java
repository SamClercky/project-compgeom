package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.DecompositionTest;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventQueueTest {

    private static ArrayList<Event<Object>> createEventsAlong(PVector direction) {
        final var events = new ArrayList<Event<Object>>();

        for (float i = 0; i < 10; i++) {
            final float finalI = i;
            var event = new Event<>() {

                @Override
                public DCVertex getVertex() {
                    return new DCVertex(direction.copy().mult(finalI));
                }

                @Override
                public Object getReason() {
                    return null;
                }
            };
            events.add(event);
        }

        return events;
    }

    private static void checkInCorrectOrder(ArrayList<Event<Object>> order, EventQueue<Object, Event<Object>> queue) {
        for (var line : order) {
            assertEquals(line, queue.poll());
        }
    }

    @Test
    void testXComparator() {
        var direction = new PVector(1, 0);
        var queue = EventQueue.fromDirection(direction);
        var order = createEventsAlong(direction);
        queue.addAll(order);
        checkInCorrectOrder(order, queue);
    }

    @Test
    void testYComparator() {
        var direction = new PVector(0, 1);
        var queue = EventQueue.fromDirection(direction);
        var order = createEventsAlong(direction);
        queue.addAll(order);
        checkInCorrectOrder(order, queue);
    }

    @Test
    void testGeneralComparator() {
        var direction = new PVector(1, 1);
        var queue = EventQueue.fromDirection(direction);
        var order = createEventsAlong(direction);
        queue.addAll(order);
        checkInCorrectOrder(order, queue);
    }

    @Test
    void testGeneralComparator2() {
        var direction = new PVector(-1, 1);
        var queue = EventQueue.fromDirection(direction);
        var order = createEventsAlong(direction);
        queue.addAll(order);
        checkInCorrectOrder(order, queue);
    }

    @Test
    void testSlab45Indent1() throws IOException {
        final var polygon = DecompositionTest.readPolygon("indented.poly");
        // Rotate points 45 degrees
        final var theta = PVector.angleBetween(new PVector(0, 1), new PVector(1, 1));
        final var queue = new EventQueue<>();
        for (var p : polygon.points()) {
            p.rotate(theta);

            queue.add(new Event<Object>() {
                @Override
                public PVector getPoint() {
                    return p;
                }

                @Override
                public DCVertex getVertex() {
                    return null;
                }

                @Override
                public Object getReason() {
                    return null;
                }
            });
        }

        assertEquals(new PVector(-67.88226f, 485.07526f), queue.poll().getPoint());
        assertEquals(new PVector(73.53909f, 642.053f), queue.poll().getPoint());
        assertEquals(new PVector(81.317276f, 484.36816f), queue.poll().getPoint());
        assertEquals(new PVector(89.80257f, 284.96405f), queue.poll().getPoint());
        assertEquals(new PVector(120.20816f, 403.05087f), queue.poll().getPoint());
        assertEquals(new PVector(265.16504f, 437.6991f), queue.poll().getPoint());
    }
}