package be.ulbvub.compgeom;

import be.ulbvub.compgeom.ui.*;
import be.ulbvub.compgeom.utils.CalculationResult;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import be.ulbvub.compgeom.utils.PolygonReader;
import be.ulbvub.compgeom.utils.PolygonWriter;
import processing.core.PApplet;
import processing.core.PVector;

import javax.swing.*;
import java.util.ArrayList;

public class MainScreen extends PApplet {
    private boolean showDCEL = false;
    private PointDrawRegion polygonRegion;
    private CalculationResult dcel;
    private Polygon minkowskiSecondShape;
    private MouseClickEvent mouseClicked = null;
    private Button btnSave;
    private Button btnOpen;
    private Button btnDecompose;
    private Button btnMinkowski;
    private Button btnReset;
    private Button btnToggleDCEL;

    public MainScreen() {
        super();
    }

    @Override
    public void settings() {
        super.settings();
        size(800, 640, P2D);
    }

    private void resetAll() {
        dcel = null;
        minkowskiSecondShape = null;
        showDCEL = false;
        polygonRegion.setPolygon(new Polygon(new ArrayList<>()));
    }

    @Override
    public void setup() {
        polygonRegion = new PointDrawRegion();

        btnSave = new Button("Save", new PVector(0, 0), new PVector(50, 20));
        btnSave.setListener((evt) -> {
            System.out.println("Save file...");
            final var polygon = polygonRegion.getPolygon();
            final var writer = new PolygonWriter(polygon);
            writer.openFileDialog();
        });

        btnOpen = new Button("Open", new PVector(0, 0), new PVector(50, 20));
        btnOpen.setListener((evt) -> {
            System.out.println("Open file...");

            resetAll();

            final var reader = new PolygonReader();
            reader.openFileDialog();
            polygonRegion.setPolygon(reader.getPolygon());
        });

        btnDecompose = new Button("Decompose", new PVector(0, 0), new PVector(90, 20));
        btnDecompose.setListener((evt) -> {
            final var frame = new DecompositionConfigFrame(polygonRegion.getPolygon());
            frame.open();
            frame.setListener((config) -> {
                showDCEL = true;
                if (config.polygon().points().size() > 3) {
                    dcel = config.decompose();
                    JOptionPane.showMessageDialog(frame, "Decomposition complete");
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Not enough vertices to successfully do a decomposition",
                            "Decomposition failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            System.out.println("Decomposing polygon");
        });

        btnMinkowski = new Button("Minkowski", new PVector(0, 0), new PVector(90, 20));
        btnMinkowski.setListener((evt) -> {
            final var frame = new MinkowskiConfigFrame(polygonRegion.getPolygon());
            frame.open();
            frame.setListener((config) -> {
                showDCEL = true;
                if (config.a().points().size() > 3 && config.b().points().size() >= 3) {
                    minkowskiSecondShape = config.b();
                    dcel = config.calculate();
                    JOptionPane.showMessageDialog(frame, "Minkowski sum complete");
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Not enough vertices to successfully do a Minkowski sum",
                            "Minkowski sum failed",
                            JOptionPane.ERROR_MESSAGE);
                }
                System.out.println("Calculating minkowski sum with config: " + config);
            });
        });

        btnReset = new Button("Reset", new PVector(0, 0), new PVector(60, 20));
        btnReset.setListener((evt) -> {
            resetAll();
        });

        btnToggleDCEL = new Button("Toggle DCEL", new PVector(0, 0), new PVector(90, 20));
        btnToggleDCEL.setListener((evt) -> {
            showDCEL = !showDCEL;
        });
    }

    @Override
    public void draw() {
        drawBackground();
        final var context = DrawContext.fromApplet(this, mouseClicked);
        mouseClicked = null; // reset event

        HBox.with(context)
                .draw((ctx) -> {
                    ctx.fill(color(240));
                    VBox.with(ctx)
                            .draw((ctx1) -> btnSave.draw(ctx1))
                            .draw((ctx2) -> btnOpen.draw(ctx2))
                            .draw((ctx3) -> btnDecompose.draw(ctx3))
                            .draw((ctx4) -> btnMinkowski.draw(ctx4))
                            .draw((ctx5) -> btnReset.draw(ctx5))
                            .draw((ctx6) -> btnToggleDCEL.draw(ctx6));
                })
                .draw((ctx) -> {
                    ctx.fill(color(255));
                    StackBox.with(ctx)
                            .draw((ctx1) -> {
                                if (dcel != null && showDCEL)
                                    dcel.draw(ctx);
                                else
                                    polygonRegion.draw(ctx);

                                if (minkowskiSecondShape != null) {
                                    minkowskiSecondShape.draw(ctx1);
                                }
                            }).draw((ctx2) -> {
                                // Draw statistics
                                final var applet = ctx2.applet();

                                final var pos = ctx2.size().copy().sub(new PVector(260, 100));

                                ctx2.applyTextStyle();
                                final var mousePosition = ctx.region().toLocalPoint(ctx.mousePosition());
                                applet.text("Number of vertices: " + polygonRegion.getPolygon().points().size() + "\n" +
                                                "Number of faces in decomposition: " + (dcel != null ? dcel.getFaceCount() : 0) + "\n" +
                                                "Number of vertices in decomposition: " + (dcel != null ? dcel.getVertexCount() : 0) + "\n" +
                                                "Number of edges in decomposition: " + (dcel != null ? dcel.getHalfEdgeCount() : 0) + "\n" +
                                                "X: " + mousePosition.x + ", Y: " + mousePosition.y,
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
