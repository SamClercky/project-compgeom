package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.DecompositionTest;
import be.ulbvub.compgeom.utils.Event;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlabDecompositionTest extends DecompositionTest {

    @Test
    void buildEventQueueSquare() {
        final var decomposition = new SlabDecomposition(new PVector(0, 1), square);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(0, 1), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(0, 0), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(1, 0), EventTypes.End, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(1, 1), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void buildEventQueueTriangle() {
        final var decomposition = new SlabDecomposition(new PVector(0, 1), triangle);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(0, 0), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(0.5f, 1.0f), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(1, 0), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void buildEventQueueRotatedSquare() {
        final var decomposition = new SlabDecomposition(new PVector(0, 1), rotatedSquare);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(0, .5f), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(.5f, 1), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(.5f, 0), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(1, .5f), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void buildEventQueueIdents() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(265, 138), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(295, 391), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(370, 200), EventTypes.ReflexPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(400, 285), EventTypes.ReflexPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(497, 122), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(506, 402), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void buildEventQueueIdentsHorizontal() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(269, 240), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(271, 448), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(343, 336), EventTypes.Join, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(413, 328), EventTypes.Split, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(516, 450), EventTypes.End, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(530, 246), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void decomposeIdemPotent() {
        final var decomposeSquare = new SlabDecomposition(new PVector(0, 1), square);
        decomposeSquare.buildEventQueue();
        decomposeSquare.run();
        final var squareResult = decomposeSquare.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, squareResult.getVertices().size());
        assertEquals(1, squareResult.getFaces().size());
        assertEquals(8, squareResult.getEdges().size());
        assertValidDecomposition(square, squareResult);

        final var decomposeTriangle = new SlabDecomposition(new PVector(0, 1), triangle);
        decomposeTriangle.buildEventQueue();
        decomposeTriangle.run();
        final var triangleResult = decomposeTriangle.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(3, triangleResult.getVertices().size());
        assertEquals(1, triangleResult.getFaces().size());
        assertEquals(6, triangleResult.getEdges().size());
        assertValidDecomposition(triangle, triangleResult);

        final var decomposeSquareRotated = new SlabDecomposition(new PVector(0, 1), square);
        decomposeSquareRotated.buildEventQueue();
        decomposeSquareRotated.run();
        final var rotatedSquareResult = decomposeSquareRotated.getDecomposition();

        // Decomposition should be idempotent
        assertEquals(4, rotatedSquareResult.getVertices().size());
        assertEquals(1, rotatedSquareResult.getFaces().size());
        assertEquals(8, rotatedSquareResult.getEdges().size());
        assertValidDecomposition(rotatedSquare, rotatedSquareResult);
    }

    @Test
    void decomposeIdents() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
        assertValidDecomposition(polygon, result);
    }

    @Test
    void decomposeIdentsY() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 0), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(9, result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(24, result.getEdges().size());
    }

    @Test
    void decomposeIdents45() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsMIN45() throws IOException {
        final var polygon = readPolygon("indented.poly");

        final var decomposition = new SlabDecomposition(new PVector(-1, -1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(9, result.getVertices().size());
        assertEquals(4, result.getFaces().size());
        assertEquals(24, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontal() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(10, result.getVertices().size());
        assertEquals(5, result.getFaces().size());
        assertEquals(28, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontalY() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 0), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontal45() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsHorizontalMIN45() throws IOException {
        final var polygon = readPolygon("indented-horizontal.poly");

        final var decomposition = new SlabDecomposition(new PVector(-1, -1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(10, result.getVertices().size());
        assertEquals(5, result.getFaces().size());
        assertEquals(28, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewed() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewedY() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 0), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewed45() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeIdentsSkewedMIN45() throws IOException {
        final var polygon = readPolygon("indented-skewed.poly");

        final var decomposition = new SlabDecomposition(new PVector(-1, -1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(8, result.getVertices().size());
        assertEquals(3, result.getFaces().size());
        assertEquals(20, result.getEdges().size());
    }

    @Test
    void decomposeCurl() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(45, result.getVertices().size());
        assertEquals(16, result.getFaces().size());
        assertEquals(120, result.getEdges().size());
    }

    @Test
    void decomposeCurlY() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 0), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(45, result.getVertices().size());
        assertEquals(16, result.getFaces().size());
        assertEquals(120, result.getEdges().size());
    }

    @Test
    void decomposeCurl45() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(45, result.getVertices().size());
        assertEquals(16, result.getFaces().size());
        assertEquals(120, result.getEdges().size());
    }

    @Test
    void decomposeCurlMIN45() throws IOException {
        final var polygon = readPolygon("curl.poly");

        final var decomposition = new SlabDecomposition(new PVector(-1, -1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(46, result.getVertices().size());
        assertEquals(16, result.getFaces().size());
        assertEquals(122, result.getEdges().size());
    }

    @Test
    void decomposeCurlInv() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(53, result.getVertices().size());
        assertEquals(18, result.getFaces().size());
        assertEquals(140, result.getEdges().size());
    }

    @Test
    void decomposeCurlInvY() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 0), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(53, result.getVertices().size());
        assertEquals(18, result.getFaces().size());
        assertEquals(140, result.getEdges().size());
    }

    @Test
    void decomposeCurlInv45() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var decomposition = new SlabDecomposition(new PVector(1, 1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(53, result.getVertices().size());
        assertEquals(18, result.getFaces().size());
        assertEquals(140, result.getEdges().size());
    }

    @Test
    void decomposeCurlInvMIN45() throws IOException {
        final var polygon = readPolygon("curl-inverted.poly");

        final var decomposition = new SlabDecomposition(new PVector(-1, -1), polygon);
        decomposition.buildEventQueue();
        decomposition.run();
        final var result = decomposition.getDecomposition();

        // Decomposition should have added 8 more half edges, 2 more vertices and 2 more faces
        assertValidDecomposition(polygon, result);
        assertEquals(54, result.getVertices().size());
        assertEquals(19, result.getFaces().size());
        assertEquals(144, result.getEdges().size());
    }

    void assertEventEquals(PVector expectedPoint, EventTypes expectedReason, Event<EventTypes> observed) {
        assertEquals(expectedPoint, observed.getPoint());
        assertEquals(expectedReason, observed.getReason());
    }
}