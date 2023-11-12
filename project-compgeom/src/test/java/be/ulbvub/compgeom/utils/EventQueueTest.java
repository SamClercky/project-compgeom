package be.ulbvub.compgeom.utils;

import org.junit.jupiter.api.Test;
import processing.core.PVector;

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
}