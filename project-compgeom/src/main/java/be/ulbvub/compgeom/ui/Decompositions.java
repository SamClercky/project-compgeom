package be.ulbvub.compgeom.ui;

import javax.swing.*;

public enum Decompositions {
    Triangulation("Triangulation"),
    Greedy("Greedy decomposition"),
    Slab("Slab decomposition"),
    Kd("Kd decomposition");


    private final String name;

    Decompositions(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static JComboBox<Decompositions> getCombobox() {
        final var options = Decompositions.values();
        final var box = new JComboBox<>(options);
        box.setSelectedIndex(0);
        return box;
    }
}
