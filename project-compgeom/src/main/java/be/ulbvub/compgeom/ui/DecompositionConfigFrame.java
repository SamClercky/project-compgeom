package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class DecompositionConfigFrame extends JFrame {
    private Consumer<DecompositionConfig> listener;
    private JPanel config;

    public DecompositionConfigFrame(final Polygon polygon) {
        super();

        setLayout(new BorderLayout());
        setTitle("Configure decomposition");

        final var contentPane = new JPanel();
        add(contentPane, BorderLayout.CENTER);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.add(new JLabel("Decomposition configuration"));

        final var algorithms = new String[]{"None selected", "Triangulation", "Slab decomposition"};
        final var algorithmSelector = new JComboBox<>(algorithms);
        algorithmSelector.setSelectedIndex(0);
        contentPane.add(algorithmSelector);
        algorithmSelector.addActionListener((evt) -> {
            config.removeAll();
            switch ((String) Objects.requireNonNull(algorithmSelector.getSelectedItem())) {
                case "Triangulation" -> config.add(new TriangulationConfigPane());
                case "Slab decomposition" -> config.add(new SlabConfigPane());
                default -> {}
            }
            pack();
        });

        config = new JPanel();
        contentPane.add(config);

        final var btnConfirm = new JButton("Decompose");
        btnConfirm.addActionListener((evt) -> {
            System.out.println("Configuration confirmed, ready for decomposition");
            if (config != null && listener != null) {
                listener.accept(((ConfigPanel) config.getComponent(0)).config(polygon));
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

    public void setListener(Consumer<DecompositionConfig> listener) {
        this.listener = listener;
    }

    public void open() {
        pack();
        setVisible(true);
    }

    private abstract static class ConfigPanel extends JPanel {
        public abstract DecompositionConfig config(Polygon polygon);
    }

    private static class SlabConfigPane extends ConfigPanel {
        private final JTextField fieldX;
        private final JTextField fieldY;
        public SlabConfigPane() {
            final var layout = new GridLayout(2, 2, 5, 5);
            setLayout(layout);
            final var labelX = new JLabel("Direction x: ");
            fieldX = new JTextField("1", 3);
            add(labelX);
            add(fieldX);

            final var labelY = new JLabel("Direction y: ");
            fieldY = new JTextField("0", 3);
            add(labelY);
            add(fieldY);
        }

        @Override
        public DecompositionConfig.SlabConfig config(Polygon polygon) {
            final var x = Float.parseFloat(fieldX.getText());
            final var y = Float.parseFloat(fieldY.getText());
            return new DecompositionConfig.SlabConfig(new PVector(x, y), polygon);
        }
    }

    private static class TriangulationConfigPane extends ConfigPanel {
        @Override
        public DecompositionConfig config(Polygon polygon) {
            return new DecompositionConfig.TriangulationConfig(polygon);
        }
    }
}
