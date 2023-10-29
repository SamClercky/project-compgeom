package be.ulbvub.compgeom.utils;

import processing.core.PMatrix2D;
import processing.core.PVector;

public class Utils {
    public static float min(float... values) {
        var result = Float.MAX_VALUE;
        for (var val : values) {
            if (val < result) {
                result = val;
            }
        }
        return result;
    }

    public static float max(float... values) {
        var result = Float.MIN_VALUE;
        for (var val : values) {
            if (val > result) {
                result = val;
            }
        }
        return result;
    }

    public static PMatrix2D getDirectionMatrix(PVector direction) {
        return new PMatrix2D(direction.x, direction.y, 0, direction.y, direction.x, 0);
    }
}
