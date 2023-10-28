package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public enum TurnDirection {
    LEFT,
    RIGHT;

    public static TurnDirection orientation(PVector a, PVector b, PVector c) {
        // assert only in 2D
        assert a.z == 0;
        assert b.z == 0;
        assert c.z == 0;

        PVector k = b.copy().sub(a);
        PVector l = c.copy().sub(b);

        // Be careful as this formula assumes a right-handed axis-system
        return (k.x * l.y - k.y * l.x) < 0 ? RIGHT : LEFT;
    }
}
