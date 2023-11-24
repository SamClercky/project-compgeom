package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PolygonReader {
    private static File lastRead;
    private final ArrayList<PVector> polygon;

    public PolygonReader() {
        polygon = new ArrayList<>();
    }

    public Polygon getPolygon() {
        return new Polygon(polygon);
    }

    public void openFileDialog() {
        final var dialog = new JFileChooser();
        if (lastRead != null && lastRead.exists() && lastRead.getParentFile().isDirectory()) {
            dialog.setCurrentDirectory(lastRead.getParentFile());
        }
        final var filter = new FileNameExtensionFilter("Polygon description files", "poly");
        dialog.setFileFilter(filter);

        final var status = dialog.showOpenDialog(null);
        switch (status) {
            case JFileChooser.APPROVE_OPTION -> {
                try {
                    final var selected = dialog.getSelectedFile();
                    lastRead = selected;
                    readFile(selected);
                } catch (IOException | RuntimeException ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Polygon read",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case JFileChooser.CANCEL_OPTION | JFileChooser.ERROR_OPTION -> {
                System.out.println("Something went wrong...");

                JOptionPane.showMessageDialog(
                        null,
                        "Could not open file to read from",
                        "Polygon read",
                        JOptionPane.ERROR_MESSAGE);
            }
            default -> {
            }
        }
    }

    public void readFile(File file) throws IOException, RuntimeException {
        if (!file.isFile() || !file.canRead()) {
            throw new IOException("Path is not a file or is not readable");
        }

        final var buffer = new BufferedReader(new FileReader(file));
        var line = buffer.readLine();
        while (line != null && !line.isEmpty()) {
            var point = line.split(";");

            try {
                polygon.add(new PVector(Float.parseFloat(point[0]), Float.parseFloat(point[1])));
            } catch (IndexOutOfBoundsException ex) {
                throw new RuntimeException("Invalid file format detected", ex);
            }

            line = buffer.readLine();
        }

        buffer.close();
    }
}
