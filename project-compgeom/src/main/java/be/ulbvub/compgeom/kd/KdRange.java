package be.ulbvub.compgeom.kd;

public record KdRange(int xStart, int xEnd, int yStart, int yEnd, boolean isX) {
    public int middleIndexX() {
        return (xStart + xEnd) / 2;
    }

    public int middleIndexY() {
        return (yStart + yEnd) / 2;
    }

    public boolean isY() {
        return !isX;
    }

    public boolean isEmpty() {
        return xStart >= yEnd || yStart >= yEnd;
    }
}
