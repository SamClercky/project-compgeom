package be.ulbvub.compgeom.utils;

public class DCHalfEdge {

    private DCHalfEdge twin;
    private DCHalfEdge next;
    private DCVertex origin;
    private DCFace face;

    public DCHalfEdge() {
    }

    public DCHalfEdge(DCVertex origin) {
        this.origin = origin;
    }


    public DCVertex getDestination() {
        return this.next.origin;
    }

    public DCHalfEdge getTwin() {
        return twin;
    }

    public void setTwin(DCHalfEdge twin) {
        this.twin = twin;
    }

    public DCHalfEdge getNext() {
        return next;
    }

    public DCHalfEdge getPrev() {
        return DoublyConnectedEdgeList.getPrevEdgeOfFace(origin, face);
    }

    public void setNext(DCHalfEdge next) {
        this.next = next;
    }

    public DCVertex getOrigin() {
        return origin;
    }

    public void setOrigin(DCVertex origin) {
        this.origin = origin;
    }

    public DCFace getFace() {
        return face;
    }

    public void setFace(DCFace face) {
        this.face = face;
    }

    public Line toLine() {
        return new Line(origin.getPoint(), twin.getOrigin().getPoint());
    }
}
