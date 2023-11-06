package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.ui.DrawContext;
import be.ulbvub.compgeom.ui.Drawable;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class PointDrawRegion implements Drawable {

    private final Polygon polygon;
    private BiConsumer<PVector, Polygon> listener;

    public PointDrawRegion() {
        polygon = new Polygon(new ArrayList<>());
    }

    public void setListener(BiConsumer<PVector, Polygon> listener) {
        this.listener = listener;
    }

    @Override
    public void draw(DrawContext context) {
        if (context.mouseClicked() != null) {
            final var point = context.region().toLocalPoint(context.mouseClicked().position());
            polygon.points().add(point);
            listener.accept(point, polygon);
        }
        polygon.draw(context);
    }

    public Polygon getPolygon() {
        return polygon;
    }

}
