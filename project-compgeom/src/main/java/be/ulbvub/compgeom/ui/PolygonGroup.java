package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.CalculationResult;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Random;

public class PolygonGroup implements CalculationResult {
    final ArrayList<Polygon> polygons;

    public PolygonGroup(ArrayList<Polygon> polygons) {
        this.polygons = polygons;
    }

    public ArrayList<Polygon> polygons() {
        return this.polygons;
    }

    @Override
    public int getFaceCount() {
        return polygons.size();
    }

    @Override
    public int getVertexCount() {
        int count = 0;
        for (var p : polygons) {
            count += p.points().size();
        }
        return count;
    }

    @Override
    public int getHalfEdgeCount() {
        int count = 0;
        for (var p : polygons) {
            count += p.points().size();
        }
        return (count - polygons.size()) * 2;
    }

    @Override
    public void draw(DrawContext context) {
        // claim all space by default as we do not have a fixed size on the polygon
        context.claimRegion(context.size());

        context.applyStyle();
        final var applet = context.applet();

        Random rand = new Random();
        for (var polygon : this.polygons) {

            // Draw faces
            rand.setSeed(polygon.hashCode());
            applet.fill(rand.nextInt(50, 200), rand.nextInt(50, 200), rand.nextInt(50, 200));
            applet.beginShape();
            for (var point : polygon.points()) {
                applet.vertex(point.x, point.y);
            }
            applet.endShape(PConstants.CLOSE);

            // Draw edges
            for (var i = 0; i < polygon.points().size(); i++) {
                var start = polygon.points().get(i);
                var end = polygon.points().get((i + 1) % polygon.points().size());
                applet.line(start.x, start.y, end.x, end.y);
            }

            // Draw vertices
            for (var point : polygon.points()) {
                applet.circle(point.x, point.y, context.style().getPointSize());
            }
        }
    }
}
