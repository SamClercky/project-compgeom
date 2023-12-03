package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import processing.core.PVector;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PolygonReader {

    public enum PrefabPolygons {
        Curl("Curl", "curl.poly"),
        CurlInverted("Inverted Curl", "curl-inverted.poly"),
        Indented("Indented", "indented.poly"),
        IndentedHorizontal("Horizontal Indented", "indented-horizontal.poly"),
        SplitJoin("Split Join", "internal-split-join.poly"),
        Saw("Saw", "zaag.poly"),
        NonConvexRect("Non-convex rectangle (Chazelle only)", "non-convex-rectangle3.poly");

        private String fileName;
        private String name;

        PrefabPolygons(final String name, final String fileName) {
            this.name = name;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return name;
        }

        public File getFile() {
            return new File(getClass().getClassLoader().getResource(fileName).getFile());
        }
    }

    private class PrefabPolygonFrame extends JDialog {
        public PrefabPolygonFrame(JFrame parent) {
            super(parent, true);

            setLayout(new BorderLayout());
            setTitle("Select prefab polygon");

            final var contentPane = new JPanel();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
            add(contentPane, BorderLayout.CENTER);

            final var selector = new JComboBox<>(PrefabPolygons.values());
            selector.setSelectedIndex(0);
            contentPane.add(selector);

            final var btnConfirm = new JButton("Open");
            btnConfirm.addActionListener((evt) -> {
                try {
                    readFile(((PrefabPolygons) selector.getSelectedItem()).getFile());
                } catch (IOException e) {
                    System.err.println("Could not open prefab polygon: " + selector.getSelectedItem());
                }

                this.dispose();
            });
            final var btnCancel = new JButton("Cancel");
            btnCancel.addActionListener((evt) -> {
                this.dispose();
            });

            final var paneBtn = new JPanel();
            paneBtn.setLayout(new BoxLayout(paneBtn, BoxLayout.LINE_AXIS));
            paneBtn.add(btnConfirm);
            paneBtn.add(btnCancel);

            contentPane.add(paneBtn);

            pack();
            setVisible(true);
        }
    }

    private static File lastRead;
    private final ArrayList<PVector> polygon;

    public PolygonReader() {
        polygon = new ArrayList<>();
    }

    public Polygon getPolygon() {
        return new Polygon(polygon);
    }

    public void openPrefabDialog() {
        new PrefabPolygonFrame(null);
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
