package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import java.awt.*;
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

        final var algorithmSelector = Decompositions.getCombobox();
        contentPane.add(algorithmSelector);
        algorithmSelector.addActionListener((evt) -> {
            config.removeAll();
            switch ((Decompositions) Objects.requireNonNull(algorithmSelector.getSelectedItem())) {
                case Triangulation -> config.add(new TriangulationConfigPane());
                case Greedy -> config.add(new GreedyConfigPane());
                case Slab -> config.add(new SlabConfigPane());
                case Kd -> config.add(new KdConfigPane());
                default -> {
                }
            }
            pack();
        });

        config = new JPanel();
        config.add(new TriangulationConfigPane());
        contentPane.add(config);

        final var btnConfirm = new JButton("Decompose");
        btnConfirm.addActionListener((evt) -> {
            System.out.println("Configuration confirmed, ready for decomposition");
            if (config != null && listener != null) {
                final var components = config.getComponents();
                final var component = components.length == 1 ? components[0] : null;
                if (component instanceof ConfigPanel cp) {
                    listener.accept(cp.config(polygon));
                } else {
                    listener.accept(null);
                }
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

    public abstract static class ConfigPanel extends JPanel {
        public abstract DecompositionConfig config(Polygon polygon);
    }

    public static class SlabConfigPane extends ConfigPanel {

        @Override
        public DecompositionConfig.SlabConfig config(Polygon polygon) {
            return new DecompositionConfig.SlabConfig(new PVector(0, 1), polygon);
        }
    }

    public static class TriangulationConfigPane extends ConfigPanel {
        @Override
        public DecompositionConfig config(Polygon polygon) {
            return new DecompositionConfig.TriangulationConfig(polygon);
        }
    }

    public static class KdConfigPane extends ConfigPanel {
        @Override
        public DecompositionConfig config(Polygon polygon) {
            return new DecompositionConfig.KdConfig(polygon);
        }
    }

    public static class GreedyConfigPane extends ConfigPanel {
        @Override
        public DecompositionConfig config(Polygon polygon) {
            return new DecompositionConfig.GreedyConfig(polygon);
        }
    }
}
