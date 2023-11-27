package be.ulbvub.compgeom.ui;

import javax.swing.*;

public enum Shapes {
    None("None selected"),
    Rectangle("Rectangle"),
    Circle("Circle"),
    Triangle("Triangle");

    private final String name;

    Shapes(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static JComboBox<Shapes> getCombobox() {
        final var options = Shapes.values();
        final var box = new JComboBox<>(options);
        box.setSelectedIndex(0);

        return box;
    }
}
