package be.ulbvub.compgeom.utils;

import processing.core.PMatrix2D;
import processing.core.PVector;

import java.util.Comparator;
import java.util.TreeSet;

public class SweepLine extends TreeSet<Line> {
    private static class XComparator implements Comparator<Line> {
        @Override
        public int compare(Line a, Line b) {
            // we assume here to only deal with simple polygons, so never crossings.
            // otherwise we cannot define a simple global ordering, which is needed here

            if (a.equals(b)) {
                return 0;
            }

            Line aLine; // Line with minimal point before minimal point bLine
            Line bLine;
            if (a.leftMost().x <= b.leftMost().x) {
                aLine = a;
                bLine = b;
            } else {
                aLine = b;
                bLine = a;
            }

            switch (TurnDirection.orientation(bLine.rightMost(), bLine.leftMost(), aLine.rightMost())) {
                case RIGHT -> {
                    return 1;
                }
                case LEFT -> {
                    return -1;
                }
                case STRAIGHT -> {
                    switch (TurnDirection.orientation(bLine.rightMost(), bLine.leftMost(), aLine.leftMost())) {
                        case RIGHT -> {
                            return 1;
                        }
                        case LEFT -> {
                            return -1;
                        }
                        case STRAIGHT -> {
                            if (aLine.leftMost().y < bLine.rightMost().y) {
                                return -1;
                            } else if (aLine.leftMost().y > bLine.rightMost().y) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    }
                }
            }

            throw new IllegalStateException("All possibilities should be handled above");
        }

    }

    private static class YComparator implements Comparator<Line> {
        private final XComparator xComparator;

        public YComparator() {
            xComparator = new XComparator();
        }

        @Override
        public int compare(Line a, Line b) {
            return xComparator.compare(a.rotate90(), b.rotate90());
        }
    }

    private static class GenericComparator implements Comparator<Line> {
        private final PMatrix2D matrix;
        private final XComparator xComparator;

        public GenericComparator(PVector direction) {
            matrix = Utils.getDirectionMatrix(direction);
            xComparator = new XComparator();
        }

        @Override
        public int compare(Line a, Line b) {
            // Transform along direction
            var aOut = new Line(
                    matrix.mult(a.start().copy(), new PVector(0, 0)),
                    matrix.mult(a.end().copy(), new PVector(0, 0)));
            var bOut = new Line(
                    matrix.mult(b.start().copy(), new PVector(0, 0)),
                    matrix.mult(b.end().copy(), new PVector(0, 0)));
            return xComparator.compare(aOut, bOut);
        }
    }


    private SweepLine(Comparator<Line> comparator) {
        super(comparator);
    }

    public SweepLine() {
        super(new XComparator());
    }

    public static SweepLine fromDirection(PVector direction) {
        if (direction.equals(new PVector(1, 0))) {
            // Optimized version for along x-axis
            return new SweepLine(new XComparator());
        } else if (direction.equals(new PVector(0, 1))) {
            // Optimized version along y-axis
            return new SweepLine(new YComparator());
        } else {
            // General solution
            return new SweepLine(new GenericComparator(direction));
        }
    }

}
