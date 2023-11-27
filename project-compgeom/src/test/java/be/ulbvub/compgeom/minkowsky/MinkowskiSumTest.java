package be.ulbvub.compgeom.minkowsky;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.ui.DecompositionConfig;
import be.ulbvub.compgeom.ui.MinkowskiConfig;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;

import static be.ulbvub.compgeom.DecompositionTest.readPolygon;
import static be.ulbvub.compgeom.minkowski.MinkowskiSum.minkowski;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinkowskiSumTest {

    DoublyConnectedEdgeList rectangle;
    DoublyConnectedEdgeList triangle;
    Polygon square;
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
        rotatedSquare = new Polygon(new ArrayList<>() {
            {
                add(new PVector(1, 0.5f));
                add(new PVector(0.5f, 0));
                add(new PVector(0, 0.5f));
                add(new PVector(0.5f, 1));
            }
        });

        rectangle = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(30, 0));
                add(new PVector(30, 20));
                add(new PVector(50, 20));
                add(new PVector(50, 0));
            }
        });
        triangle = new DoublyConnectedEdgeList(new ArrayList<>() {
            {
                add(new PVector(0, 20));
                add(new PVector(10, 30));
                add(new PVector(20, 20));
            }
        });
    }

    @Test
    public void minkowskiTriangleRectangle() {
        final DoublyConnectedEdgeList sum = minkowski(triangle, rectangle);

        // Decomposition should be idempotent
        assertEquals(6, sum.getVertices().size());
        assertEquals(12, sum.getEdges().size());
        assertEquals(1, sum.getFaces().size());
    }

    @Test
    void calculateIdents() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeIdentsHorizontal() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeIdentsSkewed() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeInternalSplitJoin() throws IOException {
        final var polygon = readPolygon("internal-split-join.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeCurl() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeCurlInv() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }

    @Test
    void decomposeSaw() throws IOException {
        final var polygon = readPolygon("zaag.poly");

        final var triConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.TriangulationConfig(null));
        triConfig.calculate(); // For now, only check if we finish
        final var greedyConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.GreedyConfig(null));
        greedyConfig.calculate(); // For now, only check if we finish
        final var slabConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.SlabConfig(new PVector(0, 1), null));
        slabConfig.calculate(); // For now, only check if we finish
        final var kdConfig = new MinkowskiConfig(polygon, square, new DecompositionConfig.KdConfig(null));
        kdConfig.calculate(); // For now, only check if we finish
    }
}
