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
}
