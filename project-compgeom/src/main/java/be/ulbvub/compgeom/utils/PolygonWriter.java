package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PolygonWriter {
    private final Polygon polygon;

    public PolygonWriter(Polygon polygon) {
        this.polygon = polygon;
    }

    public void openFileDialog() {
        final var dialog = new JFileChooser();
        final var filter = new FileNameExtensionFilter("Polygon description files", "poly");
        dialog.setFileFilter(filter);

        final var status = dialog.showSaveDialog(null);
        switch (status) {
            case JFileChooser.APPROVE_OPTION -> {
                try {
                    writeFile(dialog.getSelectedFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Polygon write",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case JFileChooser.CANCEL_OPTION | JFileChooser.ERROR_OPTION -> {
                System.out.println("Something went wrong...");

                JOptionPane.showMessageDialog(
                        null,
                        "Could not open file to write to",
                        "Polygon write",
                        JOptionPane.ERROR_MESSAGE);
            }
            default -> {}
        }
    }

    public void writeFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create a new file");
        }
        if (!file.isFile() || !file.canWrite()) {
            throw new IOException("Path is not a file or is not writable");
        }

        final var buffer = new BufferedWriter(new FileWriter(file));

        for (var point: polygon.points()) {
            buffer.write(point.x + ";" + point.y + "\n");
        }

        buffer.close();
    }
}
