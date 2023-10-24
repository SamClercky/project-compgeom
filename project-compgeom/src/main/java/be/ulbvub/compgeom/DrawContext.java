package be.ulbvub.compgeom;

import processing.core.PApplet;
import processing.core.PVector;

public record DrawContext(
        PApplet applet,
        DrawStyle style,
        Region region,
        MouseClickEvent mouseClicked,
        PVector mousePosition) {

    public static DrawContext fromApplet(PApplet applet, MouseClickEvent mouseClicked) {
        return new DrawContext(
                applet,
                new DrawStyle()
                        .withFill(applet.color(255))
                        .withStroke(applet.color(0, 0, 0))
                        .withPointSize(5),
                new Region(new PVector(0, 0), new PVector(applet.width, applet.height)),
                mouseClicked,
                new PVector(applet.mouseX, applet.mouseY));
    }

    public void applyStyle() {
        style.apply(applet);
    }

    public boolean isInside(PVector point) {
        return region.isInside(point);
    }

    public boolean isInside(PVector[] points) {
        return region.isInside(points);
    }

    public DrawContext withSize(PVector size) {
        return new DrawContext(applet, style, region.withSize(size), mouseClicked, mousePosition);
    }

    public PVector size() {
        return region.size();
    }

    public DrawContext withStart(PVector start) {
        return new DrawContext(applet, style, region.withStart(start), mouseClicked, mousePosition);
    }

    public DrawContext consumeEvent(PVector startRegion) {
        return new DrawContext(applet, style, region, null, mousePosition);
    }

    public void fill(int color) {
        applet.fill(color);
        applet.rect(0, 0, region.size().x, region.size().y);
    }

}
