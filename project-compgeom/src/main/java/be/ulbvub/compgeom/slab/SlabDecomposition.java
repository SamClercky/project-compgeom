package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Objects;

public class SlabDecomposition {
    private final SweepLine sweepLine;
    private final EventQueue<EventTypes, Event<EventTypes>> queue;
    private final DoublyConnectedEdgeList decomposition;
    private final float theta;

    public SlabDecomposition(PVector direction, Polygon polygon) {
        this.theta = PVector.angleBetween(new PVector(0, 1), direction);

        this.sweepLine = SweepLine.fromDirection(new PVector(1, 0));
        this.queue = EventQueue.fromDirection(new PVector(1, 0));

        // Transform all points, before consuming them in the DCEL
        for (var point : polygon.points()) {
            point.rotate(theta);
        }

        this.decomposition = new DoublyConnectedEdgeList(polygon);
    }

    public void buildEventQueue() {
        if (decomposition.getVertices().isEmpty()) return;

        var slidingIterator = new SlidingIterator<>(3, decomposition.getFaces().get(0).ccwIteratorEdge());

        while (slidingIterator.hasNext()) {
            var pointWindow = slidingIterator.next();
            var prevPoint = pointWindow.get(0).getOrigin();
            var currPoint = pointWindow.get(1).getOrigin();
            var nextPoint = pointWindow.get(2).getOrigin();

            // TODO: Generalize for all directions, now only X-axis
            EventTypes reason = null;

            switch (TurnDirection.orientation(prevPoint, currPoint, nextPoint)) {
                case LEFT -> {
                    if (currPoint.getPoint().x <= prevPoint.getPoint().x && currPoint.getPoint().x <= nextPoint.getPoint().x) {
                        reason = EventTypes.Start;
                    } else if (currPoint.getPoint().x >= prevPoint.getPoint().x && currPoint.getPoint().x >= nextPoint.getPoint().x) {
                        reason = EventTypes.End;
                    } else {
                        reason = EventTypes.NormalPoint;
                    }
                }
                case RIGHT -> {
                    if (currPoint.getPoint().x <= prevPoint.getPoint().x && currPoint.getPoint().x <= nextPoint.getPoint().x) {
                        reason = EventTypes.Split;
                    } else if (currPoint.getPoint().x >= prevPoint.getPoint().x && currPoint.getPoint().x >= nextPoint.getPoint().x) {
                        reason = EventTypes.Join;
                    } else {
                        reason = EventTypes.ReflexPoint;
                    }
                }
            }

            // At this point, we should now what reason is
            assert reason != null;
            final EventTypes finalReason = reason;
            queue.add(new Event<>() {
                @Override
                public DCVertex getVertex() {
                    return currPoint;
                }

                @Override
                public EventTypes getReason() {
                    return finalReason;
                }
            });
        }
    }

    public EventQueue<EventTypes, Event<EventTypes>> getQueue() {
        return this.queue;
    }

    public void run() {
        while (!queue.isEmpty()) {
            final var event = queue.poll();

            final var reason = event.getReason();
            switch (reason) {
                case Start -> {
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.add(iter.next().toLine());
                    }
                }
                case End -> {
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.remove(iter.next().toLine());
                    }
                }
                case Split -> {
                    final var toBeAdded = new ArrayList<Line>();
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        toBeAdded.add(iter.next().toLine());
                    }
                    emitReflex(event);

                    // We can't just restart iterating as we have added a new line, which can confuse the algorithm
                    sweepLine.addAll(toBeAdded);
                }
                case Join -> {
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.remove(iter.next().toLine());
                    }
                    emitReflex(event);
                }
                case NormalPoint -> {
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        final var edge = iter.next().toLine();
                        if (edge.rightMost().x <= event.getPoint().x) {
                            // Remove edges that end in current event
                            sweepLine.remove(edge);
                        } else {
                            // Edge that starts in current event, add to sweepLine
                            sweepLine.add(edge);
                        }
                    }
                }
                case ReflexPoint -> {
                    // Remove edges that end in current event
                    final var toBeAdded = new ArrayList<Line>();
                    for (var iter = event.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        final var edge = iter.next().toLine();
                        if (edge.rightMost().x <= event.getPoint().x) {
                            sweepLine.remove(edge);
                        } else {
                            toBeAdded.add(edge);
                        }
                    }

                    emitReflex(event);

                    // Edge that starts in current event, add to sweepLine
                    // We can't just restart iterating as we have added a new line, which can confuse the algorithm
                    sweepLine.addAll(toBeAdded);
                }
                default -> throw new IllegalStateException("Unexpected value: " + event.getReason());
            }
        }
    }

    private void emitReflex(Event<EventTypes> event) {
        final var reflexPoint = new Line(event.getPoint(), event.getPoint().copy().add(new PVector(1, 0)));

        final var edgeAbove = sweepLine.higher(reflexPoint);
        final var edgeBelow = sweepLine.lower(reflexPoint);

        if (edgeAbove != null)
            handleOneSidedCut(edgeAbove, event);

        if (edgeBelow != null)
            handleOneSidedCut(edgeBelow, event);
    }

    private PVector intersectionAlongSameYAxis(Line edge, PVector reflexPoint) {
        final var determinant = (edge.start().x - edge.end().x);
        if (determinant == 0) {
            return new PVector(reflexPoint.x, reflexPoint.dist(edge.start()) < reflexPoint.dist(edge.end()) ? edge.start().y : edge.end().y);
        } else {
            final var m = (edge.start().y - edge.end().y) / determinant;
            final var y = m * (reflexPoint.x - edge.start().x) + edge.start().y;

            return new PVector(reflexPoint.x, y);
        }
    }

    private void handleOneSidedCut(Line edge, Event<EventTypes> event) {
        final var intersection = intersectionAlongSameYAxis(edge, event.getPoint());

        // Query if the intersection is visible from the current reflex point to avoid cutting into the outer null face
        // Start by finding the prev and next outer adjacent edges
        final var outerEdgeIter = event.getVertex().iterateOutgoingEdges();
        while (outerEdgeIter.hasNext()) {
            final var nextOuterEdge = outerEdgeIter.next();
            if (nextOuterEdge.getFace() == null) {
                var orient1 = TurnDirection.orientationRaw(nextOuterEdge.getPrev().getOrigin().getPoint(), nextOuterEdge.getOrigin().getPoint(), intersection);
                var orient2 = TurnDirection.orientationRaw(nextOuterEdge.getNext().getOrigin().getPoint(), nextOuterEdge.getOrigin().getPoint(), intersection);

                if (orient1 < 0 && orient2 > 0) {
                    return; // Not visible, stop cut here
                } else {
                    break; // Intersection is visible, continue cut
                }
            }
        }


        DCHalfEdge halfEdge = null;
        for (var dEdge : decomposition.getEdges()) {
            final var dLine = dEdge.toLine();
            // Find the edge on the inner side
            if (dLine.equals(edge) && dEdge.getFace() != null) {
                halfEdge = dEdge;
                break;
            }
        }
        Objects.requireNonNull(halfEdge, "A half edge should exist in DCEL that partially overlaps with the provided edge: " + edge);

        // Insert steiner point if point does not yet exist
        DCVertex otherEndVertex;
        if (halfEdge.getOrigin().getPoint().equals(intersection))
            otherEndVertex = halfEdge.getOrigin();
        else if (halfEdge.getTwin().getOrigin().getPoint().equals(intersection))
            otherEndVertex = halfEdge.getTwin().getOrigin();
        else {
            otherEndVertex = decomposition.addVertex(halfEdge, intersection);

            // We just updated the DCEL, now we need to also update the sweep line to retrieve later
            // the needed details
            sweepLine.remove(edge);
            sweepLine.add(new Line(intersection, edge.rightMost()));
        }

        // Connect reflex point with above (steiner) point
        if (!otherEndVertex.getPoint().equals(event.getVertex().getPoint()))
            try {
                decomposition.addEdge(otherEndVertex, event.getVertex());
            } catch (NullPointerException ex) {
                System.out.println("Something fishy happened with point: " + intersection + ", for reflex: " + event.getPoint());
            }
    }

    public DoublyConnectedEdgeList getDecomposition() {
        // Transform back
        for (var vertex : decomposition.getVertices()) {
            vertex.getPoint().rotate(-this.theta);
        }

        return decomposition;
    }

}
