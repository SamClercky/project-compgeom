package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.PolygonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class KdDecompositionTest {

    Polygon square;
    Polygon triangle;
    Polygon rotatedSquare;

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

    @Test
    void decomposeIdemPotent() {
        final var decomposeSquare = new KdDecomposition(square);
        decomposeSquare.run();
        final var squareResult = decomposeSquare.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, squareResult.getVertices().size());
        assertEquals(1, squareResult.getFaces().size());
        assertEquals(8, squareResult.getEdges().size());

        final var decomposeTriangle = new KdDecomposition(triangle);
        decomposeTriangle.run();
        final var triangleResult = decomposeTriangle.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(3, triangleResult.getVertices().size());
        assertEquals(1, triangleResult.getFaces().size());
        assertEquals(6, triangleResult.getEdges().size());

        final var decomposeSquareRotated = new KdDecomposition(square);
        decomposeSquareRotated.run();
        final var rotatedSquareResult = decomposeSquareRotated.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, rotatedSquareResult.getVertices().size());
        assertEquals(1, rotatedSquareResult.getFaces().size());
        assertEquals(8, rotatedSquareResult.getEdges().size());
    }

    // TESTS ARE WRONG -> DUPLICATE POINTS WRONGLY COUNTED
    @Test
    void decomposeIdents() throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource("indented.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertEquals(12, result.getVertices().size());
        assertEquals(7, result.getFaces().size());
        assertEquals(18*2, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontal() throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource("indented-horizontal.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertEquals(12, result.getVertices().size());
        assertEquals(7, result.getFaces().size());
        assertEquals(18*2, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewed() throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource("indented-skewed.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertEquals(12, result.getVertices().size());
        assertEquals(7, result.getFaces().size());
        assertEquals(18*2, result.getEdges().size());
    }

    @Test
    void decomposeInternalSplitJoin() throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource("internal-split-join.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertEquals(19, result.getVertices().size());
        assertEquals(15, result.getFaces().size());
        assertEquals(66, result.getEdges().size());
    }
}