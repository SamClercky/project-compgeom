package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public enum TurnDirection {
    STRAIGHT,
    LEFT,
    RIGHT;

    public static TurnDirection orientation(DCHalfEdge a, DCHalfEdge b, DCHalfEdge c) {
        return TurnDirection.orientation(a.getOrigin(), b.getOrigin(), c.getOrigin());
    }

    public static TurnDirection orientation(DCVertex a, DCVertex b, DCVertex c) {
        return TurnDirection.orientation(a.getPoint(), b.getPoint(), c.getPoint());
    }

    public static TurnDirection orientation(PVector a, PVector b, PVector c) {
        // Be careful as this formula assumes a right-handed axis-system
        final var determinant = orientationRaw(a, b, c);
        if (determinant > 0) {
            return RIGHT;
        } else if (determinant == 0) {
            return STRAIGHT;
        } else {
            return LEFT;
        }
    }

    public static float orientationRaw(DCVertex a, DCVertex b, DCVertex c) {
        return orientationRaw(a.getPoint(), b.getPoint(), c.getPoint());
    }

    public static float orientationRaw(PVector a, PVector b, PVector c) {
        // assert only in 2D
        assert a.z == 0;
        assert b.z == 0;
        assert c.z == 0;

        PVector k = b.copy().sub(a);
        PVector l = c.copy().sub(b);

        return k.x * l.y - k.y * l.x;
    }
}
