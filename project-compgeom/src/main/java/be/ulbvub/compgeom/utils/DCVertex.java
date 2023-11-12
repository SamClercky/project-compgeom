package be.ulbvub.compgeom.utils;

import processing.core.PVector;

import java.util.Iterator;

public class DCVertex {

    private static class EdgesIterator implements Iterator<DCHalfEdge> {
        private DCHalfEdge current;
        private final DCHalfEdge startEdge;
        private boolean hasStarted;

        public EdgesIterator(DCVertex start) {
            this.startEdge = start.leavingEdge;
            this.current = startEdge;
            this.hasStarted = false;
        }

        @Override
        public boolean hasNext() {
            return !hasStarted || !current.equals(startEdge);
        }

        @Override
        public DCHalfEdge next() {
            final var result = current;
            current = current.getTwin().getNext();
            hasStarted = true;

            return result;
        }
    }

    private static class TwinEdgesIterator extends EdgesIterator {
        public TwinEdgesIterator(DCVertex start) {
            super(start);
        }

        @Override
        public DCHalfEdge next() {
            return super.next().getTwin();
        }
    }


    private final PVector point;

    private DCHalfEdge leavingEdge = null;

    public DCVertex(PVector point) {
        this.point = point;
    }

    public PVector getPoint() {
        return point;
    }

    public DCHalfEdge getLeavingEdge() {
        return leavingEdge;
    }

    //get edge from this to "vertex"
    public DCHalfEdge getEdgeTo(DCVertex vertex) {
        if (this.leavingEdge != null) {
            if (this.leavingEdge.getTwin().getOrigin() == vertex) {
                return this.leavingEdge;
            } else {
                DCHalfEdge edge = this.leavingEdge.getTwin().getNext();
                //rotate over all edges that go to this vertex
                while (!edge.equals(this.leavingEdge)) {
                    if (edge.getTwin().getOrigin() == vertex) {
                        return edge;
                    }
                    edge = edge.getTwin().getNext();
                }

            }
        }
        return null;
    }

    public void setLeavingEdge(DCHalfEdge leavingEdge) {
        this.leavingEdge = leavingEdge;
    }

    public Iterator<DCHalfEdge> iterateOutgoingEdges() {
        return new EdgesIterator(this);
    }

    public Iterator<DCHalfEdge> iterateIncomingEdges() {
        return new TwinEdgesIterator(this);
    }

    @Override
    public String toString() {
        return "(x:" + point.x + ",y:"+ point.y+")";
    }
}
