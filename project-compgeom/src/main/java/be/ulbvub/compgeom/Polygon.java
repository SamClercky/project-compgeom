package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.Utils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

public class Polygon implements Drawable {
    private final ArrayList<PVector> points;

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
    }

    public Polygon(ArrayList<PVector> points) {
        this.points = points;
    }

    public ArrayList<PVector> getPoints() {
        return points;
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
}
