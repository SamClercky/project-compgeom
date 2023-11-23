package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.utils.DCVertex;

import java.util.Objects;

public final class KdVertex {
    private final DCVertex vertex;
    private boolean hasBeenProcessed;

    public KdVertex(DCVertex vertex) {
        this.vertex = vertex;
        this.hasBeenProcessed = false;
    }

    public DCVertex vertex() {
        return vertex;
    }

    public boolean hasNotBeenProcessed() {
        return !hasBeenProcessed;
    }

    public void setProcessed() {
        hasBeenProcessed = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KdVertex) obj;
        return Objects.equals(this.vertex, that.vertex) &&
                this.hasBeenProcessed == that.hasBeenProcessed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex, hasBeenProcessed);
    }

    @Override
    public String toString() {
        return "KdVertex[" +
                "vertex=" + vertex + ", " +
                "hasBeenProcessed=" + hasBeenProcessed + ']';
    }

}
