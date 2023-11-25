package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.DecompositionTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KdDecompositionTest extends DecompositionTest {

    @Test
    void decomposeIdemPotent() {
        final var decomposeSquare = new KdDecomposition(square);
        decomposeSquare.run();
        final var squareResult = decomposeSquare.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, squareResult.getVertices().size());
        assertEquals(1, squareResult.getFaces().size());
        assertEquals(8, squareResult.getEdges().size());
        assertValidDecomposition(square, squareResult);

        final var decomposeTriangle = new KdDecomposition(triangle);
        decomposeTriangle.run();
        final var triangleResult = decomposeTriangle.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(3, triangleResult.getVertices().size());
        assertEquals(1, triangleResult.getFaces().size());
        assertEquals(6, triangleResult.getEdges().size());
        assertValidDecomposition(triangle, triangleResult);

        final var decomposeSquareRotated = new KdDecomposition(square);
        decomposeSquareRotated.run();
        final var rotatedSquareResult = decomposeSquareRotated.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, rotatedSquareResult.getVertices().size());
        assertEquals(1, rotatedSquareResult.getFaces().size());
        assertEquals(8, rotatedSquareResult.getEdges().size());
        assertValidDecomposition(rotatedSquare, rotatedSquareResult);
    }

    // TESTS ARE WRONG -> DUPLICATE POINTS WRONGLY COUNTED
    @Test
    void decomposeIdents() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(10 * 2, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontal() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(9, result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(12 * 2, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewed() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(10 * 2, result.getEdges().size());
    }

    @Test
    void decomposeInternalSplitJoin() throws IOException {
        final var polygon = readPolygon("internal-split-join.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(14, result.getVertices().size());
        assertEquals(5, result.getFaces().size());
        assertEquals(36, result.getEdges().size());
    }

    @Test
    void decomposeCurl() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(46, result.getVertices().size());
        assertEquals(17, result.getFaces().size());
        assertEquals(124, result.getEdges().size());
    }

    @Test
    void decomposeCurlInv() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(52, result.getVertices().size());
        assertEquals(17, result.getFaces().size());
        assertEquals(136, result.getEdges().size());
    }

    @Test
    void decomposeSaw() throws IOException {
        final var polygon = readPolygon("zaag.poly");

        final var decomposition = new KdDecomposition(polygon);
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(29, result.getVertices().size());
        assertEquals(12, result.getFaces().size());
        assertEquals(80, result.getEdges().size());
    }
}