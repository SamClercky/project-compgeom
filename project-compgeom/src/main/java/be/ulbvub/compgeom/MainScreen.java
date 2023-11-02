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
    private Button btnDecompose;
    private Button btnExplain;

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
        polygonRegion.setListener((newPoint, polygon) -> {
            if (polygon.points().size() > 3) {
                dcel = TriangleDecomposition.triangulateYMonotonePolygon(polygonRegion.getPolygon());
            }
        });

        btnSave = new Button("Save", new PVector(0, 0), new PVector(50, 20));
        btnSave.setListener((evt) -> {
            System.out.println("Save file...");
            return null;
        });

        btnOpen = new Button("Open", new PVector(0, 0), new PVector(50, 20));
        btnOpen.setListener((evt) -> {
            System.out.println("Open file...");
            return null;
        });

        btnDecompose = new Button("Decompose", new PVector(0, 0), new PVector(90, 20));
        btnDecompose.setListener((evt) -> {
            System.out.println("Decomposing polygon");
            return null;
        });

        btnExplain = new Button("Explain", new PVector(0, 0), new PVector(60, 20));
        btnExplain.setListener((evt) -> {
            System.out.println("Explain decomposition algorithm (tutorial part)");
            return null;
        });
    }

    @Override
    public void draw() {
        drawBackground();
        final var context = DrawContext.fromApplet(this, mouseClicked);
        mouseClicked = null; // reset event

        final var headerHeight = 40;
        HBox.with(context)
                .draw((ctx) -> {
                    ctx.fill(color(240));
                    VBox.with(ctx)
                            .draw((ctx1) -> btnSave.draw(ctx1))
                            .draw((ctx2) -> btnOpen.draw(ctx2))
                            .draw((ctx3) -> btnDecompose.draw(ctx3))
                            .draw((ctx4) -> btnExplain.draw(ctx4));
                })
                .draw((ctx) -> {
                    ctx.fill(color(255));
                    StackBox.with(ctx)
                            .draw((ctx1) -> {
                                polygonRegion.draw(ctx);
                                if (dcel != null)
                                    dcel.draw(ctx);
                            }).draw((ctx2) -> {
                                // Draw statistics
                                final var applet = ctx2.applet();

                                final var pos = ctx2.size().copy().sub(new PVector(260, 50));

                                ctx2.applyTextStyle();
                                applet.text("Number of vertices: " + polygonRegion.getPolygon().points().size() + "\n" +
                                                "Number of faces in decomposition: " + (dcel != null ? dcel.getFaces().size() : 0) + "\n" +
                                                "Number of vertices in decomposition: " + (dcel != null ? dcel.getVertices().size() : 0) + "\n" +
                                                "Number of edges in decomposition: " + (dcel != null ? dcel.getEdges().size() : 0),
                                        pos.x, pos.y);
                            });
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
