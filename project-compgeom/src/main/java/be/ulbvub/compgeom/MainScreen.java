package be.ulbvub.compgeom;

import be.ulbvub.compgeom.triangles.TriangleDecomposition;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import processing.core.PApplet;
import processing.core.PVector;

public class MainScreen extends PApplet {
    private PointDrawRegion polygonRegion;
    private DoublyConnectedEdgeList dcel;
    private MouseClickEvent mouseClicked = null;
    private Button btnSave;
    private Button btnOpen;

    public MainScreen() {
        super();
    }

    @Override
    public void settings() {
        super.settings();
        size(800, 640, P3D);
    }

    @Override
    public void setup() {
        polygonRegion = new PointDrawRegion();
        btnSave = new Button("Save", new PVector(10, 10), new PVector(50, 20));
        btnSave.setListener((evt) -> {
            System.out.println("Save file...");
            return null;
        });

        btnOpen = new Button("Open", new PVector(70, 10), new PVector(50, 20));
        btnOpen.setListener((evt) -> {
            System.out.println("Open file...");
            return null;
        });
    }

    @Override
    public void draw() {
        drawBackground();
        final var context = DrawContext.fromApplet(this, mouseClicked);
        mouseClicked = null; // reset event

        final var headerHeight = 40;
        DrawRegion.apply(new PVector(0, 0), new PVector(width, headerHeight), context, (ctx) -> {
            ctx.fill(color(240));
            btnSave.draw(ctx);
            btnOpen.draw(ctx);
            return null;
        });
        DrawRegion.apply(new PVector(0, headerHeight), new PVector(width, height - headerHeight), context, (ctx) -> {
            ctx.fill(color(255));
            polygonRegion.draw(ctx);
            int n = polygonRegion.getPolygon().points().size();
            if (n > 3 && (dcel == null || dcel.getVertices().size() != n)) {
                dcel = TriangleDecomposition.triangulateYMonotonePolygon(polygonRegion.getPolygon());

            }
            if (dcel != null)
                dcel.draw(ctx);
            return null;
        });
    }

    void drawBackground() {
        fill(color(255));
        rect(0, 0, width, height);
    }

    @Override
    public void mouseClicked() {
        this.mouseClicked = new MouseClickEvent(new PVector(mouseX, mouseY));
    }
}
