package be.ulbvub.compgeom.utils;

import processing.core.PVector;

import java.util.Objects;

public final class Line {
    private PVector start;
    private PVector end;

    public Line(PVector start, PVector end) {
        this.start = start;
        this.end = end;
    }

    public boolean intersectRay(Line ray) {
        return TurnDirection.orientationRaw(ray.start, ray.end, start) * TurnDirection.orientationRaw(ray.start, ray.end, end) <= 0;
    }

    public boolean intersects(Line other) {
        return this.intersectRay(other) && other.intersectRay(this);
    }

    public PVector intersectionPointWithRay(Line ray) {
        final var dir = ray.end.copy().sub(ray.start);
        final var lv = ray.start; // linear variation
        var alpha = (dir.y * (end.x - lv.x) - end.y * dir.x + lv.y * dir.x) / (start.y * dir.x - end.y * dir.x - dir.y * (start.x - end.x));
        if (Float.isNaN(alpha)) {
            alpha = start.x < end.x || start.y < end.y ? 0.0f : 1.0f;
        }

        // Assert that the target is on the line
        assert alpha >= 0;
        assert alpha <= 1;

        // Return found position
        return start.copy().mult(alpha).add(end.copy().mult(1 - alpha));
    }

    public float pointOnRay(PVector point) {
        final var dir = end.copy().sub(start);

        var beta = 0.0f;
        final var betaDir = point.copy().sub(start);

        if (dir.x != 0.0f) {
            beta = betaDir.x / dir.x;
        } else if (dir.y != 0.0f) {
            beta = betaDir.y / dir.y;
        }

        // Assert on ray
        assert Math.abs(betaDir.x - beta * dir.x) < 1e-4;
        assert Math.abs(betaDir.y - beta * dir.y) < 1e-4;

        return beta;
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

    public void setLeftMost(PVector v) {
        if (start.x <= end.x) {
            start = v;
        } else {
            end = v;
        }
    }

    public PVector rightMost() {
        return start.x <= end.x ? end : start;
    }

    public void setRightMost(PVector v) {
        if (start.x <= end.x) {
            end = v;
        } else {
            start = v;
        }
    }

    public Line rotate90() {
        //noinspection SuspiciousNameCombination
        return new Line(new PVector(start.y, start.x), new PVector(end.y, end.x));
    }

    public PVector start() {
        return start;
    }

    public PVector end() {
        return end;
    }

    public void setStart(PVector start) {
        this.start = start;
    }

    public void setEnd(PVector end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Line[" +
                "start=" + start + ", " +
                "end=" + end + ']';
    }

}
