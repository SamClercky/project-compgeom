package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.ArrayList;

public class SlabDecomposition {
    private final SweepLine sweepLine;
    private final EventQueue<EventTypes, Event<EventTypes>> queue;
    private final PVector direction;
    private final DoublyConnectedEdgeList decomposition;

    public SlabDecomposition(PVector direction, Polygon polygon) {
        @SuppressWarnings("SuspiciousNameCombination") final var orthogonalDirection = new PVector(direction.y, direction.x);

        this.sweepLine = SweepLine.fromDirection(orthogonalDirection);
        this.queue = EventQueue.fromDirection(orthogonalDirection);
        this.direction = direction;
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
                        reason = EventTypes.Join;
                    } else if (currPoint.getPoint().x >= prevPoint.getPoint().x && currPoint.getPoint().x >= nextPoint.getPoint().x) {
                        reason = EventTypes.Split;
                    } else {
                        reason = EventTypes.ReflexPoint;
                    }
                }
            }

            // At this point, we should now what reason is
            assert reason != null;
            reason.setVertex(currPoint);
            final EventTypes finalReason = reason;
            queue.add(new Event<>() {
                @Override
                public PVector getPoint() {
                    return currPoint.getPoint();
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
        final var decomposition = new ArrayList<Polygon>();
        while (!queue.isEmpty()) {
            final var event = queue.poll();

            final var reason = event.getReason();
            switch (reason) {
                case Start -> {
                    for (var iter = reason.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.add(iter.next().toLine());
                    }
                }
                case End -> {
                    for (var iter = reason.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.remove(iter.next().toLine());
                    }
                }
                case Split -> {
                    emitReflex(event);
                    for (var iter = reason.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.add(iter.next().toLine());
                    }
                }
                case Join -> {
                    for (var iter = reason.getVertex().iterateOutgoingEdges(); iter.hasNext(); ) {
                        sweepLine.remove(iter.next().toLine());
                    }
                    emitReflex(event);
                }
                case NormalPoint -> {
                    for (var iter = reason.getVertex().iterateIncomingEdges(); iter.hasNext(); ) {
                        final var edge = iter.next();
                        if (edge.getOrigin().getPoint().x <= event.getPoint().x) {
                            // Remove edges that end in current event
                            sweepLine.remove(edge.toLine());
                        } else {
                            // Edge that starts in current event, add to sweepLine
                            sweepLine.add(edge.toLine());
                        }
                    }
                }
                case ReflexPoint -> {
                    // Remove edges that end in current event
                    for (var iter = reason.getVertex().iterateIncomingEdges(); iter.hasNext(); ) {
                        final var edge = iter.next();
                        if (edge.getOrigin().getPoint().x <= event.getPoint().x) {
                            sweepLine.remove(edge.toLine());
                        }
                    }

                    emitReflex(event);

                    // Edge that starts in current event, add to sweepLine
                    for (var iter = reason.getVertex().iterateIncomingEdges(); iter.hasNext(); ) {
                        final var edge = iter.next();
                        if (edge.getOrigin().getPoint().x > event.getPoint().x) {
                            sweepLine.add(edge.toLine());
                        }
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + event.getReason());
            }
        }
    }

    private void emitReflex(Event<EventTypes> event) {
//        final var reflexPoint = new Line(event.getPoint(), event.getPoint());
//
//        final var edgeAbove = sweepLine.higher(reflexPoint);
//        final var edgeBelow = sweepLine.lower(reflexPoint);
//
//        if (edgeAbove != null && edgeBelow != null) {
//            final var upperIntersection = intersectionAlongSameYAxis(edgeAbove, event.getPoint());
//            final var lowerIntersection = intersectionAlongSameYAxis(edgeBelow, event.getPoint());
//
//            // Insert steiner point if point does not yet exist
//            if (!edgeAbove.start().equals(upperIntersection) && !edgeAbove.end().equals(upperIntersection))
//                decomposition.addVertex(null, null);
//            if (!edgeBelow.start().equals(lowerIntersection) && !edgeBelow.end().equals(lowerIntersection))
//                polygon.addPoint(edgeBelow, lowerIntersection);
//
//            // TODO: Cutting like this makes it no longer a simple polygon
//            final var cutOffPolygon = polygon.cutFromPointToPoint(upperIntersection, lowerIntersection);
//            decomposition.add(cutOffPolygon);
//            polygon.addPoint(new Line(upperIntersection, lowerIntersection), event.getPoint());
//        } else if (edgeAbove != null) {
//            handleOneSidedIntersection(edgeAbove, event.getPoint(), decomposition);
//        } else if (edgeBelow != null) {
//            handleOneSidedIntersection(edgeBelow, event.getPoint(), decomposition);
//        }

    }

//    private PVector intersectionAlongSameYAxis(Line edge, PVector reflexPoint) {
//        final var determinant = (edge.start().x - edge.end().x);
//        if (determinant == 0) {
//            return new PVector(reflexPoint.x, reflexPoint.dist(edge.start()) < reflexPoint.dist(edge.end()) ? edge.start().y : edge.end().y);
//        } else {
//            final var m = (edge.start().y - edge.end().y) / determinant;
//            final var y = m * (reflexPoint.x - edge.start().x) + edge.start().y;
//
//            return new PVector(reflexPoint.x, y);
//        }
//    }
//
//    private void handleOneSidedIntersection(Line edge, PVector reflexPoint) {
//        final var intersection = intersectionAlongSameYAxis(edge, reflexPoint);
//
//        // Insert if needed Steiner point
//        if (!edge.start().equals(intersection) && !edge.end().equals(intersection))
//            polygon.addPoint(edge, intersection);
//
//        final var cutOffPolygon = polygon.cutFromPointToPoint(intersection, reflexPoint);
//        decomposition.add(cutOffPolygon);
//    }
}
