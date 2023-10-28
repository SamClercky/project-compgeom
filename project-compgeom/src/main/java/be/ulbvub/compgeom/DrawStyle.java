package be.ulbvub.compgeom;

import processing.core.PApplet;

public class DrawStyle implements Cloneable {
    private int fill;
    private int stroke;
    private int textColor;
    private float pointSize;

    public DrawStyle() {
        this.fill = 0;
        this.stroke = 0;
        this.textColor = 0;
        pointSize = 5;
    }

    public DrawStyle withFill(int fill) {
        this.fill = fill;
        return this;
    }

    public DrawStyle withStroke(int stroke) {
        this.stroke = stroke;
        return this;
    }

    public DrawStyle withPointSize(float size) {
        this.pointSize = size;
        return this;
    }

    public DrawStyle withTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public void apply(PApplet context) {
        context.fill(fill);
        context.stroke(stroke);
    }

    public void applyText(PApplet context) {
        context.fill(textColor);
    }

    public float getPointSize() {
        return pointSize;
    }

    @Override
    protected DrawStyle clone() throws CloneNotSupportedException {
        return ((DrawStyle) super.clone());
    }
}
