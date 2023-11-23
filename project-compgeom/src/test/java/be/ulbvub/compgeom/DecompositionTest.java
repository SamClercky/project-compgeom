package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import be.ulbvub.compgeom.utils.PolygonReader;
import org.junit.jupiter.api.BeforeEach;
import processing.core.PVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class DecompositionTest {

    public Polygon square;
    public Polygon triangle;
    public Polygon rotatedSquare;

    @BeforeEach
    void setUp() {
        square = new Polygon(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(1, 0));
                add(new PVector(1, 1));
                add(new PVector(0, 1));
            }
        });
        triangle = new Polygon(new ArrayList<>() {
            {
                add(new PVector(0, 0));
                add(new PVector(0.5f, 1));
                add(new PVector(1, 0));
            }
        });
        rotatedSquare = new Polygon(new ArrayList<>() {
            {
                add(new PVector(1, 0.5f));
                add(new PVector(0.5f, 0));
                add(new PVector(0, 0.5f));
                add(new PVector(0.5f, 1));
            }
        });
    }

    public void assertValidDecomposition(Polygon source, DoublyConnectedEdgeList dcel) {
        // Check if DCEL is convex for every face
        for (var face : dcel.getFaces()) {
            assertTrue(face.isConvex(), "All faces should be convex for it to be a correct decomposition");
        }

        // Check Euler's Formula for a planar graph
        // The reason that we put this equal to 1 is because we do not add the outer face to the DCEL, so there will always be one face short
        assertEquals(1, dcel.getVertices().size() - dcel.getEdges().size() / 2 + dcel.getFaces().size(), "DCEL is not a planar graph as Euler's formula does not hold");
        assertEquals(0, dcel.getEdges().size() % 2, "DCEL should have an even amount of half edges");
    }

    public Polygon readPolygon(String fileName) throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource(fileName).getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        return reader.getPolygon();
    }
}
