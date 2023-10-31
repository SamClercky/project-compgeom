package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.Line;
import be.ulbvub.compgeom.utils.Utils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

public record Polygon(ArrayList<PVector> points) implements Drawable {
    private static class ForwardIterator implements Iterator<PVector> {
        private final int startIndex;
        private final ArrayList<PVector> points;
        private int currentStep = 0;

        public ForwardIterator(int startIndex, ArrayList<PVector> points) {
            this.startIndex = startIndex;
            this.points = points;
        }

        @Override
        public boolean hasNext() {
            return points.size() > currentStep;
        }

        @Override
        public PVector next() {
            if (!hasNext()) return null;

            final var index = (startIndex + currentStep) % points.size();
            final var point = points.get(index);
            currentStep++;

            return point;
        }

        @Override
        public void remove() {
            if (currentStep == 0) return; // nop if we did not yet yield anything

            final var previousStep = currentStep - 1;
            final var index = (startIndex + previousStep) % points.size();
            points.remove(index);
        }
    }

    private static class BackwardIterator implements Iterator<PVector> {
        private final int startIndex;
        private final ArrayList<PVector> points;
        private int currentStep = 0;

        public BackwardIterator(int startIndex, ArrayList<PVector> points) {
            this.startIndex = startIndex;
            this.points = points;
        }

        @Override
        public boolean hasNext() {
            return points.size() > currentStep;
        }

        @Override
        public PVector next() {
            if (!hasNext()) return null;

            final var index = (startIndex - currentStep + points.size()) % points.size();
            final var point = points.get(index);
            currentStep++;

            return point;
        }

        @Override
        public void remove() {
            if (currentStep == 0) return; // nop if we did not yet yield anything

            final var previousStep = currentStep - 1;
            final var index = (startIndex - previousStep + points.size()) % points.size();
            points.remove(index);
        }
    }

    @Override
    public void draw(DrawContext context) {
        context.applyStyle();
        final var applet = context.applet();

        for (var point : points) {
            applet.circle(point.x, point.y, context.style().getPointSize());
        }

        for (var i = 0; i < points.size(); i++) {
            var start = points.get(i);
            var end = points.get((i + 1) % points.size());
            applet.line(start.x, start.y, end.x, end.y);
        }
    }

    public int getLeftMostIndex() {
        var index = 0;
        for (var i = 0; i < points.size(); i++) {
            if (points.get(i).x < points.get(index).x) {
                index = i;
            }
        }

        return index;
    }

    public int getLeftMostAlongDirection(PVector direction) {
        final var matrix = Utils.getDirectionMatrix(direction);
        var index = 0;
        var leftMostTransformed = matrix.mult(points.get(0), new PVector(0, 0));

        for (var i = 0; i < points.size(); i++) {
            var transformedPoint = matrix.mult(points.get(i), new PVector(0, 0));
            if (transformedPoint.x < leftMostTransformed.x) {
                index = i;
                leftMostTransformed = matrix.mult(points.get(i), new PVector(0, 0));
            }
        }

        return index;
    }

    public PVector getPreviousFromIndex(int index) {
        return points.get((index - 1 + points.size()) % points.size());
    }

    public PVector getNextFromIndex(int index) {
        return points.get((index + 1) % points.size());
    }

    public Iterator<PVector> iterateFrom(int index) {
        return new ForwardIterator(index, points);
    }

    public Iterator<PVector> iterateFromBack(int index) {
        return new BackwardIterator(index, points);
    }

    /**
     * Cuts the polygon in 2 pieces in ccw direction.
     * The 2 resulting polygons will share a common edge along which has been cut.
     * Note that this will confuse currently running iterators over the polygon, so
     * don't use this inside an iterator without first making a clone
     *
     * @param start The first to start cutting from
     * @param end   The last point to stop cutting
     * @return The cut polygon. If polygon has less than 3 points, no points will be cut
     */
    public Polygon cutFromPointToPoint(PVector start, PVector end) {
        if (points.size() <= 2) return new Polygon(new ArrayList<>());

        final var resultingPolygon = new ArrayList<PVector>();
        final var leftMost = getLeftMostIndex();
        final var point2 = getNextFromIndex(leftMost);
        final var pointN = getPreviousFromIndex(leftMost);
        final var iterator = point2.y > pointN.y ? iterateFromBack(leftMost) : iterateFrom(leftMost);

        var hasStartPassed = false;
        while (iterator.hasNext()) {
            final var next = iterator.next();

            if (!hasStartPassed) {
                if (next.equals(start)) {
                    hasStartPassed = true;
                    resultingPolygon.add(next.copy());
                }
            } else {
                resultingPolygon.add(next.copy());

                if (next.equals(end)) {
                    break;
                } else {
                    iterator.remove();
                }
            }
        }

        return new Polygon(resultingPolygon);
    }

    /**
     * Add a point on an edge. Asserts that the edge is not zero length.
     * This will be from the polygon's standpoint a nop if the edge is not part of the polygon.
     *
     * @param edge     The edge on which a point will be inserted
     * @param newPoint The new point
     */
    public void addPoint(Line edge, PVector newPoint) {
        assert !edge.start().equals(edge.end());

        for (var currIndex = 0; currIndex < points.size(); currIndex++) {
            final var prevIndex = (currIndex - 1 + points.size()) % points.size();
            final var nextIndex = (currIndex + 1) % points.size();

            if (edge.start().equals(points.get(currIndex)) && edge.end().equals(points.get(nextIndex))) {
                points.add(nextIndex, newPoint);
            } else if (edge.start().equals(points.get(prevIndex)) && edge.end().equals(points.get(currIndex))) {
                points.add(currIndex, newPoint);
            } else if (edge.end().equals(points.get(currIndex)) && edge.start().equals(points.get(nextIndex))) {
                points.add(nextIndex, newPoint);
            } else if (edge.end().equals(points.get(prevIndex)) && edge.start().equals(points.get(currIndex))) {
                points.add(currIndex, newPoint);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object clone() {
        return new Polygon((ArrayList<PVector>) points.clone());
    }
}
