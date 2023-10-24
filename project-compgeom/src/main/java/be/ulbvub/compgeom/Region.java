package be.ulbvub.compgeom;

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

    public Region withSize(PVector size) {
        return new Region(start, size);
    }

    public Region withStart(PVector start) {
        return new Region(start, size);
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
