package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.Utils;
import processing.core.PVector;

public record Region(PVector start, PVector size) {
    public boolean isInside(PVector point) {
        final var transformedPoint = point.copy().sub(start);
        return transformedPoint.x >= 0 &&
                transformedPoint.x < size.x &&
                transformedPoint.y >= 0 &&
                transformedPoint.y < size.y;
    }

    public boolean isInside(PVector[] points) {
        for (final var point : points) {
            if (!isInside(point))
                return false;
        }
        return true;
    }

    public static Region emptyRegion() {
        return new Region(new PVector(0, 0), new PVector(0, 0));
    }

    public Region withSize(float width, float height) {
        return withSize(new PVector(width, height));
    }

    public Region withSize(PVector size) {
        final var newSize = new PVector(Utils.max(size.x, 0f), Utils.max(size.y, 0f));
        return new Region(start, newSize);
    }

    public Region withStart(PVector start) {
        return new Region(start, size);
    }

    public Region withStart(float x, float y) {
        return withStart(new PVector(x, y));
    }

    public PVector toLocalPoint(PVector point) {
        return point.copy().sub(start);
    }

    public PVector toGlobalPoint(PVector point) {
        return point.copy().add(start);
    }

    public Region relativeRegion(PVector start, PVector size) {
        return new Region(this.start.copy().add(start), size);
    }
}
