package be.ulbvub.compgeom.utils;

import processing.core.PMatrix2D;
import processing.core.PVector;

import java.util.Comparator;
import java.util.TreeSet;

public class SweepLine extends TreeSet<Line> {
    public static class XComparator implements Comparator<Line> {

        private Line alignRay(Line ray) {
            if (ray.isAlongY()) {
                return ray.start().y <= ray.end().y ? ray : ray.getInverted();
            } else {
                return ray.start().x <= ray.end().x ? ray : ray.getInverted();
            }
        }

//        @Override
//        public int compare(Line a, Line b) {
//            // we assume here to only deal with simple polygons, so never crossings.
//            // otherwise we cannot define a simple global ordering, which is needed here
//
//            final var aRay = alignRay(a);
//            final var bRay = alignRay(b);
//
//            final var alpha = aRay.rayIntersectsRay(bRay);
//            final var intersection = aRay.pointAlongRay(alpha);
//            final var beta = bRay.pointOnRay(intersection);
//            final var aPoint = aRay.pointAlongRay(-alpha);
//            final var bPoint = bRay.pointAlongRay(-beta);
//
//            final var orient = TurnDirection.orientationRaw(aPoint, intersection, bPoint);
//            if (orient != 0) {
//                return Float.compare(.0f, orient);
//            } else {
//                if (aRay.end().y < bRay.end().y) {
//                    return 1;
//                } else if (aRay.end().y > bRay.end().y) {
//                    return -1;
//                } else return Float.compare(bRay.start().y, aRay.start().y);
//            }
//        }

        @Override
        public int compare(Line a, Line b) {
            // we assume here to only deal with simple polygons, so never crossings.
            // otherwise we cannot define a simple global ordering, which is needed here

            if (a.equals(b)) {
                return 0;
            }

            Line aLine; // Line with minimal point before minimal point bLine
            Line bLine;
            int inverted = 1;
            if (a.leftMost().x <= b.leftMost().x) {
                aLine = a;
                bLine = b;
                inverted = -1;
            } else {
                aLine = b;
                bLine = a;
                inverted = 1;
            }

//            if (bLine.isAlongY()) {
//                return aLine.rightMost().y <= bLine.end().y ? -1 * inverted : inverted;
//            }

            switch (TurnDirection.orientation(bLine.rightMost(), bLine.leftMost(), aLine.rightMost())) {
                case RIGHT -> {
                    return 1 * inverted;
                }
                case LEFT -> {
                    return -1 * inverted;
                }
                case STRAIGHT -> {
                    switch (TurnDirection.orientation(bLine.rightMost(), bLine.leftMost(), aLine.leftMost())) {
                        case RIGHT -> {
                            return 1 * inverted;
                        }
                        case LEFT -> {
                            return -1 * inverted;
                        }
                        case STRAIGHT -> {
                            if (aLine.leftMost().y < bLine.rightMost().y) {
                                return -1 * inverted;
                            } else if (aLine.leftMost().y > bLine.rightMost().y) {
                                return 1 * inverted;
                            } else {
                                return -1 * inverted;
                            }
                        }
                    }
                }
            }

            throw new IllegalStateException("All possibilities should be handled above");
        }

    }

    public static class YComparator implements Comparator<Line> {
        private final XComparator xComparator;

        public YComparator() {
            xComparator = new XComparator();
        }

        @Override
        public int compare(Line a, Line b) {
            return xComparator.compare(a.rotate90(), b.rotate90());
        }
    }

    public static class GenericComparator implements Comparator<Line> {
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
