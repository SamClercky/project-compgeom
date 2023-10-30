package be.ulbvub.compgeom.utils;

public class DCFace {

    DCHalfEdge refEdge;
    public DCFace(){}
    public DCFace(DCHalfEdge refEdge){
        this.refEdge =refEdge;
    }

    public DCHalfEdge getRefEdge() {
        return refEdge;
    }

    public void setRefEdge(DCHalfEdge refEdge) {
        this.refEdge = refEdge;
    }

    int edgeLength(){
        if(this.refEdge == null) return 0;
        DCHalfEdge currEdge = this.refEdge;
        int len = 1;
        while(currEdge.getNext() != this.refEdge){
            len++;
            currEdge = currEdge.getNext();
        }
        return len;
    }


}
