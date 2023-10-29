package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.utils.Line;

import java.util.ArrayList;

public enum EventTypes {
    Start(),
    End(),
    Split(),
    Join(),
    NormalPoint(),
    ReflexPoint();

    private final ArrayList<Line> connectedEdges;

    EventTypes() {
        connectedEdges = new ArrayList<>();
    }

    public ArrayList<Line> edges() {
        return connectedEdges;
    }
}
