package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Stack;

public class KdDecomposition {

    private final ArrayList<DCVertex> xReflex;
    private final ArrayList<DCVertex> yReflex;
    private final DoublyConnectedEdgeList decomposition;

    public KdDecomposition(Polygon polygon) {
        xReflex = new ArrayList<>();
        yReflex = new ArrayList<>();
        decomposition = new DoublyConnectedEdgeList(polygon);

        findReflexPoints(); // O(n)
        // Sort now, so we have the correct order
        xReflex.sort(new DCVertexXComparator()); // O(n log(n))
        yReflex.sort(new DCVertexYComparator()); // O(n log(n))
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
        final var agenda = new Stack<KdRange>();
        agenda.push(new KdRange(0, xReflex.size(), 0, yReflex.size(), true));

        while (!agenda.isEmpty()) {
            final var range = agenda.pop();
            if (range.isEmpty())
                continue;

            if (range.isX()) {
                // X-axis
                final var middleIndexX = range.middleIndexX();
                final var middleReflex = xReflex.get(middleIndexX);
                System.out.println("Reflex handled X: " + middleReflex.toString());
                final var ray = new Line(middleReflex.getPoint(), middleReflex.getPoint().copy().add(new PVector(0, 1)));
                insertEdge(middleReflex, ray);

                agenda.push(new KdRange(range.xStart(), middleIndexX, range.yStart(), range.yEnd(), false));
                agenda.push(new KdRange(middleIndexX + 1, range.xEnd(), range.yStart(), range.yEnd(), false));
            } else {
                // Y-axis
                final var middleIndexY = range.middleIndexY();
                final var middleReflex = yReflex.get(middleIndexY);
                System.out.println("Reflex handled Y: " + middleReflex.toString());
                final var ray = new Line(middleReflex.getPoint(), middleReflex.getPoint().copy().add(new PVector(1, 0)));
                insertEdge(middleReflex, ray);

                agenda.push(new KdRange(range.xStart(), range.xEnd(), range.yStart(), middleIndexY, true));
                agenda.push(new KdRange(range.xStart(), range.xEnd(), middleIndexY + 1, range.yEnd(), true));
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
                        minLowerBeta = beta;
                    } else if (beta > 0 && beta < minUpperBeta) {
                        minUpperEdge = nextEdge;
                        minUpperBeta = beta;
                    }
                    // else beta == 0 -> ignore as this is the current edge
                }
            }
        }

        // Ok we have found here now possibly an upper and a lower edge -> connect
        if (minUpperEdge != null) {
            final var intersection = minUpperEdge.toLine().intersectionPointWithRay(ray);
            final var newVertex = getOrInsertVertex(minUpperEdge, intersection);
            decomposition.addEdge(reflexPoint, newVertex);
        }
        if (minLowerEdge != null) {
            final var intersection = minLowerEdge.toLine().intersectionPointWithRay(ray);
            final var newVertex = getOrInsertVertex(minLowerEdge, intersection);
            decomposition.addEdge(reflexPoint, newVertex);
        }
    }

    public DoublyConnectedEdgeList getDecomposition() {
        return decomposition;
    }

    private DCVertex getOrInsertVertex(DCHalfEdge edge, PVector newPoint) {
        if (edge.getOrigin().getPoint().equals(newPoint))
            return edge.getOrigin();
        else if (edge.getTwin().getOrigin().getPoint().equals(newPoint))
            return edge.getTwin().getOrigin();
        else
            return decomposition.addVertex(edge, newPoint);
    }
}
