package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.Event;
import be.ulbvub.compgeom.utils.PolygonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlabDecompositionTest {
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
        assertEventEquals(new PVector(.5f, 0), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(.5f, 1), EventTypes.NormalPoint, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(1, .5f), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    @Test
    void buildEventQueueIdents() throws IOException {
        final var polygonResource = getClass().getClassLoader().getResource("indented.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

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
        final var polygonResource = getClass().getClassLoader().getResource("indented-horizontal.poly").getFile();
        final var reader = new PolygonReader();
        reader.readFile(new File(polygonResource));
        final var polygon = reader.getPolygon();

        final var decomposition = new SlabDecomposition(new PVector(0, 1), polygon);
        decomposition.buildEventQueue();
        final var queue = decomposition.getQueue();

        assertEventEquals(new PVector(269, 240), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(271, 448), EventTypes.Start, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(343, 336), EventTypes.Split, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(413, 328), EventTypes.Join, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(516, 450), EventTypes.End, Objects.requireNonNull(queue.poll()));
        assertEventEquals(new PVector(530, 246), EventTypes.End, Objects.requireNonNull(queue.poll()));
    }

    void assertEventEquals(PVector expectedPoint, EventTypes expectedReason, Event<EventTypes> observed) {
        assertEquals(expectedPoint, observed.getPoint());
        assertEquals(expectedReason, observed.getReason());
    }

    @Test
    void run() {
    }
}