package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.ArrayList;

public class Polygon implements Drawable {
    private final ArrayList<PVector> points;

    public Polygon(ArrayList<PVector> points) {
        this.points = points;
    }

    public ArrayList<PVector> getPoints() {
        return points;
    }

    @Override
    public void draw(DrawContext context) {
        context.applyStyle();
        final var applet = context.applet();

        for (var point : points) {
            applet.circle(point.x, point.y, context.style().getPointSize());
        }

        for (var i = 0; i < points.size(); i++) {
            var start = points.get(i);
            var end = points.get((i + 1) % points.size());
            applet.line(start.x, start.y, end.x, end.y);
        }
    }
}
