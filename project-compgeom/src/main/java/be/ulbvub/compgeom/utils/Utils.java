package be.ulbvub.compgeom.utils;

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
}
