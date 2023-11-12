package be.ulbvub.compgeom.slab;

import be.ulbvub.compgeom.utils.DCVertex;

public enum EventTypes {
    Start(),
    End(),
    Split(),
    Join(),
    NormalPoint(),
    ReflexPoint();

    private DCVertex vertex;

    EventTypes() {
    }

    public DCVertex getVertex() {
        return vertex;
    }

    public void setVertex(DCVertex vertex) {
        this.vertex = vertex;
    }
}
