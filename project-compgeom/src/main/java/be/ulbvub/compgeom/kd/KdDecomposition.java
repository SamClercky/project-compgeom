package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

public class KdDecomposition {

    private final MedianQueue<DCVertex> xReflex;
    private final MedianQueue<DCVertex> yReflex;
    private final DoublyConnectedEdgeList decomposition;

    public KdDecomposition(Polygon polygon) {
        xReflex = new MedianQueue<>(new DCVertexXComparator());
        yReflex = new MedianQueue<>(new DCVertexYComparator());
        decomposition = new DoublyConnectedEdgeList(polygon);

        findReflexPoints();
    }

    private void findReflexPoints() {
        final var initialFace = decomposition.getFaces().get(0);
        final var edgeIterator = initialFace.ccwIteratorEdge();
        final var windowIterator = new SlidingIterator<>(3, edgeIterator);
        while (windowIterator.hasNext()) {
            final var nextWindow = windowIterator.next();
            final var prev = nextWindow.get(0);
            final var current = nextWindow.get(1);
            final var next = nextWindow.get(2);

            if (TurnDirection.orientation(prev.getOrigin(), current.getOrigin(), next.getOrigin()).equals(TurnDirection.RIGHT)) {
                // We have detected a reflex point, add them to sorted trees
                xReflex.add(current.getOrigin());
                yReflex.add(current.getOrigin());
            }
        }
    }

    public void run() {
        boolean alongXAxis = true;

        while (!xReflex.isEmpty() || !yReflex.isEmpty()) {
            if (alongXAxis && !xReflex.isEmpty()) {
                final var reflexPoint = xReflex.poll();
                yReflex.remove(reflexPoint); // only 1 time per point
                final var ray = new Line(reflexPoint.getPoint(), new PVector(0, 1));

                insertEdge(reflexPoint, ray);

                alongXAxis = false; // alternate to other
            } else if (!alongXAxis && !yReflex.isEmpty()) {
                final var reflexPoint = yReflex.poll();
                xReflex.remove(reflexPoint); // only 1 time per point
                final var ray = new Line(reflexPoint.getPoint(), new PVector(1, 0));

                insertEdge(reflexPoint, ray);

                alongXAxis = true; // alternate to other
            }
        }
    }

    private void insertEdge(DCVertex reflexPoint, Line ray) {

        DCHalfEdge minUpperEdge = null;
        float minUpperBeta = Float.MAX_VALUE;
        DCHalfEdge minLowerEdge = null;
        float minLowerBeta = -Float.MAX_VALUE;

        final var outgoingEdgeIterator = reflexPoint.iterateOutgoingEdges();
        while (outgoingEdgeIterator.hasNext()) {
            final var nextFace = outgoingEdgeIterator.next().getFace();
            if (nextFace == null) continue;

            final var faceIterator = nextFace.iterateForwardEdges();
            while (faceIterator.hasNext()) {
                final var nextEdge = faceIterator.next();
                final var nextLine = nextEdge.toLine();

                if (nextLine.intersectRay(ray)) {
                    final var intersectionPoint = nextLine.intersectionPointWithRay(ray);
                    final var beta = ray.pointOnRay(intersectionPoint);

                    if (beta < 0 && beta > minLowerBeta) {
                        // lower edge found
                        minLowerEdge = nextEdge;
                    } else if (beta > 0 && beta < minUpperBeta) {
                        minUpperEdge = nextEdge;
                    }
                    // else beta == 0 -> ignore as this is the current edge
                }
            }
        }

        // Ok we have found here now possibly an upper and a lower edge -> connect
        if (minUpperEdge != null) {
            final var intersection = minUpperEdge.toLine().intersectionPointWithRay(ray);
            final var newVertex = decomposition.addVertex(minUpperEdge, intersection);
            decomposition.addEdge(reflexPoint, newVertex);
        }
        if (minLowerEdge != null) {
            final var intersection = minLowerEdge.toLine().intersectionPointWithRay(ray);
            final var newVertex = decomposition.addVertex(minLowerEdge, intersection);
            decomposition.addEdge(reflexPoint, newVertex);
        }
    }

    public DoublyConnectedEdgeList getDecomposition() {
        return decomposition;
    }
}
