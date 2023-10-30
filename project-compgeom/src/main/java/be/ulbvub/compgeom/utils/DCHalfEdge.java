package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public class DCHalfEdge {

    private DCHalfEdge twin;
    private DCHalfEdge next;
    private DCVertex origin;
    private DCFace face;

    public DCHalfEdge(){}
    public DCHalfEdge(DCVertex origin){
        this.origin = origin;
    }


    public DCVertex getDestination(){
        return this.next.origin;
    }

    public DCHalfEdge getPrevious(){
        return this.twin.next.twin;
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

}
