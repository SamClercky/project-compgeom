package be.ulbvub.compgeom.utils;

import processing.core.PMatrix2D;
import processing.core.PVector;

import java.util.Comparator;
import java.util.PriorityQueue;

public class EventQueue<R, E extends Event<R>> extends PriorityQueue<E> {
    private static class XComparator<R, E extends Event<R>> implements Comparator<E> {
        @Override
        public int compare(E e1, E e2) {
            var p1 = e1.getPoint();
            var p2 = e2.getPoint();

            return Float.compare(p1.x, p2.x);
        }
    }

    private static class YComparator<R, E extends Event<R>> implements Comparator<E> {
        @Override
        public int compare(E e1, E e2) {
            var p1 = e1.getPoint();
            var p2 = e2.getPoint();

            return Float.compare(p1.y, p2.y);
        }
    }

    private static class GenericComparator<R, E extends Event<R>> implements Comparator<E> {
        private final PMatrix2D matrix;

        public GenericComparator(PVector direction) {
            matrix = Utils.getDirectionMatrix(direction);
        }

        @Override
        public int compare(E e1, E e2) {
            // Transform along direction
            var p1 = matrix.mult(e1.getPoint(), new PVector(0, 0));
            var p2 = matrix.mult(e2.getPoint(), new PVector(0, 0));

            return Float.compare(p1.x, p2.x);
        }
    }

    private EventQueue(Comparator<E> comparator) {
        super(comparator);
    }

    public EventQueue() {
        super(new XComparator<>());
    }

    public static <R, E extends Event<R>> EventQueue<R, E> fromDirection(PVector direction) {
        if (direction.equals(new PVector(1, 0))) {
            // Optimized version for along x-axis
            return new EventQueue<>(new XComparator<>());
        } else if (direction.equals(new PVector(0, 1))) {
            // Optimized version along y-axis
            return new EventQueue<>(new YComparator<>());
        } else {
            // General solution
            return new EventQueue<>(new GenericComparator<>(direction));
        }
    }
}
