package be.ulbvub.compgeom.utils;

import java.util.Iterator;
import java.util.Objects;

public class DCFace {

    private static class ForwardEdgeIterator implements Iterator<DCHalfEdge> {
        private DCHalfEdge current;
        private boolean hasStarted = false;
        private final DCFace face;

        public ForwardEdgeIterator(DCFace face) {
            this.face = face;
            this.current = face.getRefEdge();
        }

        @Override
        public boolean hasNext() {
            return !hasStarted || !current.equals(face.getRefEdge());
        }

        @Override
        public DCHalfEdge next() {
            Objects.requireNonNull(current.getNext(), "Inconsistency detected in DCEL: All half edges in a face should have a next");
            final var result = current;
            current = current.getNext();
            hasStarted = true;

            return result;
        }
    }

    private static class BackwardEdgeIterator implements Iterator<DCHalfEdge> {
        private DCHalfEdge current;
        private boolean hasStarted = false;
        private final DCFace face;

        public BackwardEdgeIterator(DCFace face) {
            this.face = face;
            this.current = face.getRefEdge();
        }

        @Override
        public boolean hasNext() {
            return !hasStarted || !current.equals(face.getRefEdge());
        }

        @Override
        public DCHalfEdge next() {
            final var previous = current.getPrev();

            final var result = current;
            current = previous;
            hasStarted = true;

            return result;
        }
    }

    private static class ForwardVertexIterator implements Iterator<DCVertex> {
        private final ForwardEdgeIterator edgeIterator;

        public ForwardVertexIterator(DCFace face) {
            edgeIterator = new ForwardEdgeIterator(face);
        }


        @Override
        public boolean hasNext() {
            return edgeIterator.hasNext();
        }

        @Override
        public DCVertex next() {
            return edgeIterator.next().getOrigin();
        }
    }

    private static class BackwardVertexIterator implements Iterator<DCVertex> {
        private final BackwardEdgeIterator edgeIterator;

        public BackwardVertexIterator(DCFace face) {
            edgeIterator = new BackwardEdgeIterator(face);
        }


        @Override
        public boolean hasNext() {
            return edgeIterator.hasNext();
        }

        @Override
        public DCVertex next() {
            return edgeIterator.next().getOrigin();
        }
    }

    DCHalfEdge refEdge;

    public DCFace() {
    }

    public DCFace(DCHalfEdge refEdge) {
        this.refEdge = refEdge;
    }

    public DCHalfEdge getRefEdge() {
        return refEdge;
    }

    public void setRefEdge(DCHalfEdge refEdge) {
        this.refEdge = refEdge;
    }

    int edgeLength() {
        if (this.refEdge == null) return 0;
        DCHalfEdge currEdge = this.refEdge;
        int len = 1;
        while (currEdge.getNext() != this.refEdge) {
            len++;
            currEdge = currEdge.getNext();
        }
        return len;
    }

    public DCHalfEdge getLeftMostEdge() {
        DCHalfEdge min = refEdge;

        for (var iter = iterateForwardEdges(); iter.hasNext(); ) {
            final var next = iter.next();

            if (next.getOrigin().getPoint().x < min.getOrigin().getPoint().x) {
                min = next;
            }
        }

        return min;
    }

    public Iterator<DCHalfEdge> iterateForwardEdges() {
        return new ForwardEdgeIterator(this);
    }

    public Iterator<DCHalfEdge> iterateBackwardEdges() {
        return new BackwardEdgeIterator(this);
    }

    public Iterator<DCVertex> iterateForwardVertices() {
        return new ForwardVertexIterator(this);
    }

    public Iterator<DCVertex> iterateBackwardVertices() {
        return new BackwardVertexIterator(this);
    }

    /**
     * @return An iterator that iterates in ccw order. The iterator starts at the left most-point.
     */
    public Iterator<DCHalfEdge> ccwIteratorEdge() {
        final var leftmost = getLeftMostEdge();
        final var point1 = leftmost.getOrigin().getPoint();
        final var point2 = leftmost.getNext().getOrigin().getPoint();
        final var pointN = leftmost.getPrev().getOrigin().getPoint();

        switch (TurnDirection.orientation(point2, point1, pointN)) {
            case STRAIGHT -> {
                // All three points are on a straight line, but no edges nor points overlap
                if (point1.y < point2.y) {
                    return iterateBackwardEdges();
                } else {
                    return iterateForwardEdges();
                }
            }
            case RIGHT -> {
                return iterateForwardEdges();
            }
            case LEFT -> {
                return iterateBackwardEdges();
            }
        }

        throw new IllegalStateException();
    }

    /**
     * @return An iterator that iterates in ccw order. The iterator starts at the left most-point.
     */
    public Iterator<DCVertex> ccwIteratorVertex() {
        final var leftmost = getLeftMostEdge();
        final var point1 = leftmost.getOrigin().getPoint();
        final var point2 = leftmost.getNext().getOrigin().getPoint();
        final var pointN = leftmost.getPrev().getOrigin().getPoint();

        switch (TurnDirection.orientation(point2, point1, pointN)) {
            case STRAIGHT -> {
                // All three points are on a straight line, but no edges nor points overlap
                if (point1.y < point2.y) {
                    return iterateBackwardVertices();
                } else {
                    return iterateForwardVertices();
                }
            }
            case RIGHT -> {
                return iterateForwardVertices();
            }
            case LEFT -> {
                return iterateBackwardVertices();
            }
        }

        throw new IllegalStateException();
    }
}
