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

            // Transform along direction
            var aOut = a.getAABB();
            var bOut = b.getAABB();

            if (aOut.max().x <= bOut.min().x) {
                return 1;
            } else if (bOut.max().x <= aOut.min().x) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private static class YComparator implements Comparator<Line> {
        @Override
        public int compare(Line a, Line b) {
            // we assume here to only deal with simple polygons, so never crossings.
            // otherwise we cannot define a simple global ordering, which is needed here

            // Transform along direction
            var aOut = a.getAABB();
            var bOut = b.getAABB();

            if (aOut.max().y <= bOut.min().y) {
                return 1;
            } else if (bOut.max().y <= aOut.min().y) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private static class GenericComparator implements Comparator<Line> {
        private final PMatrix2D matrix;

        public GenericComparator(PVector direction) {
            matrix = Utils.getDirectionMatrix(direction);
        }

        @Override
        public int compare(Line a, Line b) {
            // we assume here to only deal with simple polygons, so never crossings.
            // otherwise we cannot define a simple global ordering, which is needed here

            // Transform along direction
            var aOut = new Line(
                    matrix.mult(a.start().copy(), new PVector(0, 0)),
                    matrix.mult(a.end().copy(), new PVector(0, 0))).getAABB();
            var bOut = new Line(
                    matrix.mult(b.start().copy(), new PVector(0, 0)),
                    matrix.mult(b.end().copy(), new PVector(0, 0))).getAABB();

            if (aOut.max().x <= bOut.min().x) {
                return 1;
            } else if (bOut.max().x <= aOut.min().x) {
                return -1;
            } else {
                return 0;
            }
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
