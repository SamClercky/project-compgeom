package be.ulbvub.compgeom.triangles;

import be.ulbvub.compgeom.DecompositionTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TriangleDecompositionTest extends DecompositionTest {
    @Test
    void decomposeIdemPotentTriangle() {
        final var triangleResult = TriangleDecomposition.decompose(triangle, false);

        // Decomposition should be idempotent
        assertEquals(triangle.points().size(), triangleResult.getVertices().size());
        assertEquals(1, triangleResult.getFaces().size());
        assertEquals(6, triangleResult.getEdges().size());
        assertValidDecomposition(triangle, triangleResult);
    }

    @Test
    void decomposeIdemPotentGreedy() {
        final var triangleResult = TriangleDecomposition.decompose(triangle, true);

        // Decomposition should be idempotent
        assertEquals(triangle.points().size(), triangleResult.getVertices().size());
        assertEquals(1, triangleResult.getFaces().size());
        assertEquals(6, triangleResult.getEdges().size());
        assertValidDecomposition(triangle, triangleResult);
    }

    @Test
    void decomposeIdentsTriangle() throws IOException {
        final var polygon = readPolygon("indented.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(18, result.getEdges().size());
    }

    @Test
    void decomposeIdentsGreedy() throws IOException {
        final var polygon = readPolygon("indented.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(2, result.getFaces().size());
        assertEquals(14, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontalTriangle() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(18, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontalGreedy() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(16, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewedTriangle() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(18, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewedGreedy() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(18, result.getEdges().size());
    }

    @Test
    void decomposeInternalSplitJoinTriangle() throws IOException {
        final var polygon = readPolygon("internal-split-join.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(8, result.getFaces().size());
        assertEquals(34, result.getEdges().size());
    }

    @Test
    void decomposeInternalSplitJoinGreedy() throws IOException {
        final var polygon = readPolygon("internal-split-join.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(7, result.getFaces().size());
        assertEquals(32, result.getEdges().size());
    }

    @Test
    void decomposeCurlTriangle() throws IOException {
        final var polygon = readPolygon("curl.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(28, result.getFaces().size());
        assertEquals(114, result.getEdges().size());
    }

    @Test
    void decomposeCurlGreedy() throws IOException {
        final var polygon = readPolygon("curl.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(28, result.getFaces().size());
        assertEquals(114, result.getEdges().size());
    }

    @Test
    void decomposeCurlInvTriangle() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");
        final var result = TriangleDecomposition.decompose(polygon, false);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(34, result.getFaces().size());
        assertEquals(138, result.getEdges().size());
    }

    @Test
    void decomposeCurlInvGreedy() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");
        final var result = TriangleDecomposition.decompose(polygon, true);

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(polygon.points().size(), result.getVertices().size());
        assertEquals(34, result.getFaces().size());
        assertEquals(138, result.getEdges().size());
    }
}