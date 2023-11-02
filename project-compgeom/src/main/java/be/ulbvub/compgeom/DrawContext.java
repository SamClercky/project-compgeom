package be.ulbvub.compgeom;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Objects;

public final class DrawContext {
    private final PApplet applet;
    private final DrawStyle style;
    private final Region region;
    private Region claimedRegion;
    private final MouseClickEvent mouseClicked;
    private final PVector mousePosition;

    public DrawContext(
            PApplet applet,
            DrawStyle style,
            Region region,
            Region claimedRegion,
            MouseClickEvent mouseClicked,
            PVector mousePosition) {
        this.applet = applet;
        this.style = style;
        this.region = region;
        this.claimedRegion = claimedRegion;
        this.mouseClicked = mouseClicked;
        this.mousePosition = mousePosition;
    }

    public static DrawContext fromApplet(PApplet applet, MouseClickEvent mouseClicked) {
        return new DrawContext(
                applet,
                new DrawStyle()
                        .withFill(applet.color(255))
                        .withStroke(applet.color(0, 0, 0))
                        .withPointSize(5)
                        .withTextColor(applet.color(0)),
                new Region(new PVector(0, 0), new PVector(applet.width, applet.height)),
                new Region(new PVector(0, 0), new PVector(0, 0)),
                mouseClicked,
                new PVector(applet.mouseX, applet.mouseY));
    }

    public void applyStyle() {
        style.apply(applet);
    }

    public void applyTextStyle() {
        style.applyText(applet);
    }

    public boolean isInside(PVector point) {
        return region.isInside(point);
    }

    public boolean isInside(PVector[] points) {
        return region.isInside(points);
    }

    public void setClaimedRegion(float width, float height) {
        this.claimedRegion = claimedRegion.withSize(width, height);
    }

    public void claimRegion(PVector size) {
        claimRegion(size.x, size.y);
    }

    public void claimRegion(float width, float height) {
        this.claimedRegion = claimedRegion.withSize(
                claimedRegion.size().x + width,
                claimedRegion.size().y + height
        );
    }

    public DrawContext withSize(PVector size) {
        return new DrawContext(applet, style, region.withSize(size), claimedRegion, mouseClicked, mousePosition);
    }

    public PVector size() {
        return region.size();
    }

    public DrawContext withStart(PVector start) {
        return new DrawContext(applet, style, region.withStart(start), claimedRegion, mouseClicked, mousePosition);
    }

    public DrawContext consumeEvent(PVector startRegion) {
        return new DrawContext(applet, style, region, claimedRegion, null, mousePosition);
    }

    public Region unclaimedRegion() {
        return Region.emptyRegion()
                .withStart(this.region().start().copy().add(this.claimedRegion().size()))
                .withSize(this.region().size().copy().sub(this.claimedRegion().size()));
    }

    public DrawContext withNoClaim() {
        return new DrawContext(applet, style, region, Region.emptyRegion(), mouseClicked, mousePosition);
    }

    public void fill(int color) {
        applet.fill(color);
        applet.rect(0, 0, region.size().x, region.size().y);
    }

    public PApplet applet() {
        return applet;
    }

    public DrawStyle style() {
        return style;
    }

    public Region region() {
        return region;
    }

    public Region claimedRegion() {
        return claimedRegion;
    }

    public MouseClickEvent mouseClicked() {
        return mouseClicked;
    }

    public PVector mousePosition() {
        return mousePosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DrawContext) obj;
        return Objects.equals(this.applet, that.applet) &&
                Objects.equals(this.style, that.style) &&
                Objects.equals(this.region, that.region) &&
                Objects.equals(this.claimedRegion, that.claimedRegion) &&
                Objects.equals(this.mouseClicked, that.mouseClicked) &&
                Objects.equals(this.mousePosition, that.mousePosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applet, style, region, claimedRegion, mouseClicked, mousePosition);
    }

    @Override
    public String toString() {
        return "DrawContext[" +
                "applet=" + applet + ", " +
                "style=" + style + ", " +
                "region=" + region + ", " +
                "claimedRegion=" + claimedRegion + ", " +
                "mouseClicked=" + mouseClicked + ", " +
                "mousePosition=" + mousePosition + ']';
    }


}
