package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.ArrayList;

public class SlabDecomposition {
    private final SweepLine sweepLine;
    private final EventQueue<EventTypes, Event<EventTypes>> queue;
    private final ArrayList<Polygon> decomposition;
    private final PVector direction;

    public SlabDecomposition(PVector direction) {
        this.sweepLine = SweepLine.fromDirection(direction);
        this.queue = EventQueue.fromDirection(direction);
        this.decomposition = new ArrayList<>();
        this.direction = direction;
    }

    public void buildEventQueue(Polygon polygon) {
        if (polygon.getPoints().isEmpty()) return;

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

    public void run() {
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
                    emitReflex(event);
                    sweepLine.addAll(reason.edges());
                }
                case Join -> {
                    reason.edges().forEach(sweepLine::remove);
                    emitReflex(event);
                }
                case NormalPoint -> {
                    sweepLine.remove(reason.edges().get(0));
                    sweepLine.add(reason.edges().get(1));
                }
                case ReflexPoint -> {
                    sweepLine.remove(reason.edges().get(0));
                    emitReflex(event);
                    sweepLine.add(reason.edges().get(1));
                }
                default -> throw new IllegalStateException("Unexpected value: " + event.getReason());
            }
        }
    }

    private void emitReflex(Event<EventTypes> event) {
    }
}
