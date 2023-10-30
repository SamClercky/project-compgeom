package be.ulbvub.compgeom;

import java.util.ArrayList;

public class PointDrawRegion implements Drawable {

    private final Polygon polygon;

    public PointDrawRegion() {
        polygon = new Polygon(new ArrayList<>());
    }

    @Override
    public void draw(DrawContext context) {
        if (context.mouseClicked() != null) {
            final var point = context.region().toLocalPoint(context.mouseClicked().position());
            polygon.getPoints().add(point);
        }
        polygon.draw(context);
    }
    public Polygon getPolygon() {
        return polygon;
    }

}
