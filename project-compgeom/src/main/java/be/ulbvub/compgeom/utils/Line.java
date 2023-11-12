package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public record Line(PVector start, PVector end) {

    public boolean intersects(Line other) {
        return false;
    }

    public AABB getAABB() {
        return new AABB(
                new PVector(
                        Utils.min(start.x, end.x),
                        Utils.min(start.y, end.y)
                ),
                new PVector(
                        Utils.max(start.x, end.x),
                        Utils.max(start.y, end.y)
                )
        );
    }

    public Line getInverted() {
        return new Line(end, start);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Line other) {
            return (start.equals(other.start) && end.equals(other.end)) || (end.equals(other.start) && start.equals(other.end));
        } else {
            return false;
        }
    }

    public PVector leftMost() {
        return start.x <= end.x ? start : end;
    }

    public PVector rightMost() {
        return start.x <= end.x ? end : start;
    }

    public Line rotate90() {
        //noinspection SuspiciousNameCombination
        return new Line(new PVector(start.y, start.x), new PVector(end.y, end.x));
    }
}
