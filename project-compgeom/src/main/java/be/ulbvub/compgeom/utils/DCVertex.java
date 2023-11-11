package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public class DCVertex {


    private final PVector point;

    private DCHalfEdge leavingEdge = null;

    public DCVertex(PVector point){
        this.point = point;
    }

    public PVector getPoint() {
        return point;
    }

    public DCHalfEdge getLeavingEdge() {
        return leavingEdge;
    }
    //get edge from this to "vertex"
    public DCHalfEdge getEdgeTo(DCVertex vertex){
        if(this.leavingEdge != null){
            if(this.leavingEdge.getTwin().getOrigin() == vertex){
                return this.leavingEdge;
            }else{
                DCHalfEdge edge = this.leavingEdge.getTwin().getNext();
                //rotate over all edges that go to this vertex
                while(!edge.equals(this.leavingEdge)){
                    if(edge.getTwin().getOrigin() == vertex){
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


}
