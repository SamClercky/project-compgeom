package be.ulbvub.compgeom.minkowsky;

import be.ulbvub.compgeom.DecompositionTest;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PVector;

import java.util.ArrayList;

import static be.ulbvub.compgeom.minkowski.MinkowskiSum.minkowski;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinkowskiSumTest extends DecompositionTest {

    DoublyConnectedEdgeList rectangle;
    DoublyConnectedEdgeList triangle;

    @BeforeEach
    void setUp() {
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
    public void minkowskiTriangleRectangle(){
        final DoublyConnectedEdgeList sum = minkowski(triangle, rectangle);

        // Decomposition should be idempotent
        assertEquals(6, sum.getVertices().size());
        assertEquals(12, sum.getEdges().size());
        assertEquals(1, sum.getFaces().size());
    }

}
