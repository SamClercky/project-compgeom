package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class MinkowskiConfigFrame extends JFrame {
    private Consumer<MinkowskiConfig> listener;
    private JPanel decomposeConfig;
    private JPanel shapeConfig;

    public MinkowskiConfigFrame(final Polygon polygon) {
        super();

        setLayout(new BorderLayout());
        setTitle("Configure Minkowski");

        final var contentPane = new JPanel();
        add(contentPane, BorderLayout.CENTER);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.add(new JLabel("Decomposition configuration"));

        final var algorithmSelector = Decompositions.getCombobox();
        contentPane.add(algorithmSelector);
        algorithmSelector.addActionListener((evt) -> {
            decomposeConfig.removeAll();
            switch ((Decompositions) Objects.requireNonNull(algorithmSelector.getSelectedItem())) {
                case Triangulation -> decomposeConfig.add(new DecompositionConfigFrame.TriangulationConfigPane());
                case Greedy -> decomposeConfig.add(new DecompositionConfigFrame.GreedyConfigPane());
                case Slab -> decomposeConfig.add(new DecompositionConfigFrame.SlabConfigPane());
                case Kd -> decomposeConfig.add(new DecompositionConfigFrame.KdConfigPane());
                default -> {
                }
            }
            pack();
        });

        decomposeConfig = new JPanel();
        decomposeConfig.add(new DecompositionConfigFrame.TriangulationConfigPane());
        contentPane.add(decomposeConfig);

        final var shapeSelector = Shapes.getCombobox();
        contentPane.add(shapeSelector);
        shapeSelector.addActionListener((evt) -> {
            shapeConfig.removeAll();
            switch ((Shapes) Objects.requireNonNull(shapeSelector.getSelectedItem())) {
                case Rectangle -> shapeConfig.add(new RectanglePanel());
                case Circle -> shapeConfig.add(new CirclePanel());
                case Triangle -> shapeConfig.add(new TrianglePanel());
                default -> {
                }
            }
            pack();
        });

        shapeConfig = new JPanel();
        shapeConfig.add(new RectanglePanel());
        contentPane.add(shapeConfig);

        final var btnConfirm = new JButton("Calculate");
        btnConfirm.addActionListener((evt) -> {
            System.out.println("Configuration confirmed, ready for calculating the Minkowski sum");
            if (decomposeConfig != null && listener != null) {
                final var components = decomposeConfig.getComponents();
                final var component = components.length == 1 ? components[0] : null;
                final var shapeComponents = shapeConfig.getComponents();
                final var shapeComponent = shapeComponents.length == 1 ? shapeComponents[0] : null;

                DecompositionConfig dc;
                Polygon secondShape;
                if (component instanceof DecompositionConfigFrame.ConfigPanel cp) {
                    dc = cp.config(null);
                } else {
                    listener.accept(null);
                    return;
                }
                if (shapeComponent instanceof ShapePanel sp) {
                    secondShape = sp.config();
                } else {
                    listener.accept(null);
                    return;
                }

                listener.accept(new MinkowskiConfig(polygon, secondShape, dc));
            }
            this.dispose();
        });
        final var btnCancel = new JButton("Cancel");
        btnCancel.addActionListener((evt) -> {
            System.out.println("Configuration canceled");
            this.dispose();
        });

        final var paneBtn = new JPanel();
        paneBtn.setLayout(new BoxLayout(paneBtn, BoxLayout.LINE_AXIS));
        paneBtn.add(btnConfirm);
        paneBtn.add(btnCancel);

        contentPane.add(paneBtn);
    }

    public void open() {
        setVisible(true);
        pack();
    }

    public void setListener(Consumer<MinkowskiConfig> c) {
        this.listener = c;
    }

    public abstract static class ShapePanel extends JPanel {
        public abstract Polygon config();
    }

    public static class RectanglePanel extends ShapePanel {
        private final JTextField widthField;
        private final JTextField heightField;

        public RectanglePanel() {
            setLayout(new GridLayout(2, 2, 5, 5));
            final var widthLbl = new JLabel("Width (px): ");
            widthField = new JTextField("50", 3);
            add(widthLbl);
            add(widthField);

            final var heightLbl = new JLabel("Height (px): ");
            heightField = new JTextField("50", 3);
            add(heightLbl);
            add(heightField);
        }

        @Override
        public Polygon config() {
            final var points = new ArrayList<PVector>();
            float width;
            float height;
            try {
                width = Float.parseFloat(widthField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                width = 50; // Default value
            }
            try {
                height = Float.parseFloat(heightField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                height = 50;
            }

            points.add(new PVector(0, 0));
            points.add(new PVector(width, 0));
            points.add(new PVector(width, height));
            points.add(new PVector(0, height));

            return new Polygon(points);
        }
    }

    public static class CirclePanel extends ShapePanel {
        private final JTextField radiusField;
        private final JTextField stepsField;

        public CirclePanel() {
            setLayout(new GridLayout(2, 2, 5, 5));
            final var widthLbl = new JLabel("Radius (px): ");
            radiusField = new JTextField("50", 3);
            add(widthLbl);
            add(radiusField);

            final var stepLbl = new JLabel("Steps: ");
            stepsField = new JTextField("20", 3);
            add(stepLbl);
            add(stepsField);
        }

        @Override
        public Polygon config() {
            final var points = new ArrayList<PVector>();
            float radius;
            int steps;
            try {
                radius = Float.parseFloat(radiusField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                radius = 50; // Default value
            }
            try {
                steps = Integer.parseInt(stepsField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                steps = 50; // Default value
            }

            for (var i = 0f; i < Math.TAU; i += (float) (Math.TAU / (float) steps)) {
                points.add(new PVector((float) (radius * Math.cos(i)), (float) (radius * Math.sin(i)))
                        .add(new PVector(radius, radius)));
            }

            return new Polygon(points);
        }
    }

    public static class TrianglePanel extends ShapePanel {
        private final JTextField widthField;
        private final JTextField heightField;

        public TrianglePanel() {
            setLayout(new GridLayout(2, 2, 5, 5));
            final var widthLbl = new JLabel("Width (px): ");
            widthField = new JTextField("50", 3);
            add(widthLbl);
            add(widthField);

            final var heightLbl = new JLabel("Height (px): ");
            heightField = new JTextField("50", 3);
            add(heightLbl);
            add(heightField);
        }

        @Override
        public Polygon config() {
            final var points = new ArrayList<PVector>();
            float width;
            float height;
            try {
                width = Float.parseFloat(widthField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                width = 50; // Default value
            }
            try {
                height = Float.parseFloat(heightField.getText());
            } catch (NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                height = 50;
            }

            points.add(new PVector(0, 0));
            points.add(new PVector(width / 2, height));
            points.add(new PVector(width, 0));

            return new Polygon(points);
        }
    }
}
