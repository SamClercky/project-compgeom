package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.ArrayList;

public class SlabDecomposition {
    private final SweepLine sweepLine;
    private final EventQueue<EventTypes, Event<EventTypes>> queue;
    private final PVector direction;
    /**
     * Copy of original polygon in which we can cut without affecting the original polygon
     */
    private final Polygon workPolygon;

    public SlabDecomposition(PVector direction, Polygon polygon) {
        this.sweepLine = SweepLine.fromDirection(direction);
        this.queue = EventQueue.fromDirection(direction);
        this.direction = direction;
        this.workPolygon = new Polygon(polygon.points());
    }

    public void buildEventQueue(Polygon polygon) {
        if (polygon.points().isEmpty()) return;

        // We will start from the left-most along a certain direction but optimize along the x-axis
        var leftMostIndex = direction.equals(new PVector(1, 0)) ? polygon.getLeftMostIndex() : polygon.getLeftMostAlongDirection(direction);

        // Determine if we need to inverse the left/right logic
        var pointN = polygon.getPreviousFromIndex(leftMostIndex);
        var point2 = polygon.getNextFromIndex(leftMostIndex);
        var ccwIterator = (pointN.y < point2.y) ? polygon.iterateFromBack(leftMostIndex) : polygon.iterateFrom(leftMostIndex);
        var slidingIterator = new SlidingIterator<>(3, ccwIterator);

        // process the first point
        var firstPoint = ccwIterator.next();
        var firstReason = EventTypes.Start;
        firstReason.edges().add(new Line(pointN, firstPoint));
        firstReason.edges().add(new Line(firstPoint, point2));
        queue.add(new Event<>() {
            @Override
            public PVector getPoint() {
                return firstPoint;
            }

            @Override
            public EventTypes getReason() {
                return EventTypes.Start;
            }
        });


        while (slidingIterator.hasNext()) {
            var pointWindow = slidingIterator.next();
            var prevPoint = pointWindow[0];
            var currPoint = pointWindow[1];
            var nextPoint = pointWindow[2];

            // TODO: Generalize for all directions, now only X-axis
            EventTypes reason = null;
            if (prevPoint.y <= currPoint.y && nextPoint.y <= currPoint.y) {
                switch (TurnDirection.orientation(prevPoint, currPoint, nextPoint)) {
                    case LEFT -> {
                        reason = EventTypes.NormalPoint;
                    }
                    case RIGHT -> {
                        reason = EventTypes.ReflexPoint;
                    }
                }
            } else if (prevPoint.y > currPoint.y && nextPoint.y > currPoint.y) {
                switch (TurnDirection.orientation(prevPoint, currPoint, nextPoint)) {
                    case LEFT -> {
                        reason = EventTypes.NormalPoint;
                    }
                    case RIGHT -> {
                        reason = EventTypes.ReflexPoint;
                    }
                }
            } else if (prevPoint.x <= currPoint.x && nextPoint.x <= currPoint.x) {
                switch (TurnDirection.orientation(prevPoint, currPoint, nextPoint)) {
                    case LEFT -> {
                        reason = EventTypes.Join;
                    }
                    case RIGHT -> {
                        reason = EventTypes.End;
                    }
                }
            } else if (prevPoint.x > currPoint.x && nextPoint.x > currPoint.x) {
                switch (TurnDirection.orientation(prevPoint, currPoint, nextPoint)) {
                    case LEFT -> {
                        reason = EventTypes.Split;
                    }
                    case RIGHT -> {
                        reason = EventTypes.Start;
                    }
                }
            }

            // At this point, we should now what reason is
            assert reason != null;
            reason.edges().add(new Line(prevPoint, currPoint));
            reason.edges().add(new Line(currPoint, nextPoint));
            final EventTypes finalReason = reason;
            queue.add(new Event<>() {
                @Override
                public PVector getPoint() {
                    return currPoint;
                }

                @Override
                public EventTypes getReason() {
                    return finalReason;
                }
            });
        }
    }

    public ArrayList<Polygon> run() {
        final var decomposition = new ArrayList<Polygon>();
        while (!queue.isEmpty()) {
            final var event = queue.poll();

            final var reason = event.getReason();
            switch (reason) {
                case Start -> {
                    sweepLine.addAll(reason.edges());
                }
                case End -> {
                    reason.edges().forEach(sweepLine::remove);
                }
                case Split -> {
                    emitReflex(event, decomposition);
                    sweepLine.addAll(reason.edges());
                }
                case Join -> {
                    reason.edges().forEach(sweepLine::remove);
                    emitReflex(event, decomposition);
                }
                case NormalPoint -> {
                    sweepLine.remove(reason.edges().get(0));
                    sweepLine.add(reason.edges().get(1));
                }
                case ReflexPoint -> {
                    sweepLine.remove(reason.edges().get(0));
                    emitReflex(event, decomposition);
                    sweepLine.add(reason.edges().get(1));
                }
                default -> throw new IllegalStateException("Unexpected value: " + event.getReason());
            }
        }

        return decomposition;
    }

    private void emitReflex(Event<EventTypes> event, ArrayList<Polygon> decomposition) {
        final var reflexPoint = new Line(event.getPoint(), event.getPoint());

        final var edgeAbove = sweepLine.higher(reflexPoint);
        final var edgeBelow = sweepLine.lower(reflexPoint);

        if (edgeAbove != null && edgeBelow != null) {
            final var upperIntersection = intersectionAlongSameYAxis(edgeAbove, event.getPoint());
            final var lowerIntersection = intersectionAlongSameYAxis(edgeBelow, event.getPoint());

            // Insert steiner point if point does not yet exist
            if (!edgeAbove.start().equals(upperIntersection) && !edgeAbove.end().equals(upperIntersection))
                workPolygon.addPoint(edgeAbove, upperIntersection);
            if (!edgeBelow.start().equals(lowerIntersection) && !edgeBelow.end().equals(lowerIntersection))
                workPolygon.addPoint(edgeBelow, lowerIntersection);

            // TODO: Cutting like this makes it no longer a simple polygon
            final var cutOffPolygon = workPolygon.cutFromPointToPoint(upperIntersection, lowerIntersection);
            decomposition.add(cutOffPolygon);
            workPolygon.addPoint(new Line(upperIntersection, lowerIntersection), event.getPoint());
        } else if (edgeAbove != null) {
            handleOneSidedIntersection(edgeAbove, event.getPoint(), decomposition);
        } else if (edgeBelow != null) {
            handleOneSidedIntersection(edgeBelow, event.getPoint(), decomposition);
        }

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

    private void handleOneSidedIntersection(Line edge, PVector reflexPoint, ArrayList<Polygon> decomposition) {
        final var intersection = intersectionAlongSameYAxis(edge, reflexPoint);

        // Insert if needed Steiner point
        if (!edge.start().equals(intersection) && !edge.end().equals(intersection))
            workPolygon.addPoint(edge, intersection);

        final var cutOffPolygon = workPolygon.cutFromPointToPoint(intersection, reflexPoint);
        decomposition.add(cutOffPolygon);
    }
}