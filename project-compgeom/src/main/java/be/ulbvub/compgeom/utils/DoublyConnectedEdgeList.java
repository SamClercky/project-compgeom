package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.DrawContext;
import be.ulbvub.compgeom.Drawable;
import processing.core.PVector;

import javax.swing.*;
import java.util.ArrayList;

public class DoublyConnectedEdgeList implements Drawable {

    ArrayList<DCHalfEdge> edges = new ArrayList<DCHalfEdge>();
    ArrayList<DCVertex> vertices = new ArrayList<DCVertex>();
    ArrayList<DCFace> faces = new ArrayList<DCFace>();

    //take as argument a list of connected points
    public DoublyConnectedEdgeList(ArrayList<PVector> points){

        if(!points.isEmpty()) {
            DCFace face = new DCFace();
            faces.add(face);
            DCHalfEdge prevEdge = null;
            DCHalfEdge prevTwin = null;

            for (PVector point : points) {
                DCVertex currVertex = new DCVertex(point);
                vertices.add(currVertex);

                DCHalfEdge currEdge = new DCHalfEdge(currVertex);
                DCHalfEdge currTwin = new DCHalfEdge();
                edges.add(currEdge);
                edges.add(currTwin);

                currEdge.setFace(face);
                //no face for twin ("outside" face)
                currEdge.setTwin(currTwin);
                currTwin.setTwin(currEdge);
                currTwin.setNext(prevTwin);
                currVertex.setLeavingEdge(currEdge);

                if(prevEdge != null)
                    prevEdge.setNext(currEdge);

                if(prevTwin != null)
                    prevTwin.setOrigin(currVertex);

                prevEdge = currEdge;
                prevTwin = currTwin;


            }
            prevTwin.setOrigin(vertices.get(0));

            DCHalfEdge firstEdge = edges.get(0);
            prevEdge.setNext(firstEdge);
            DCHalfEdge firstTwin = firstEdge.getTwin();
            firstTwin.setNext(prevTwin);

            face.setRefEdge(firstEdge);
        }

    }

    /*
      How it works :
      Try on incoming edge, if it is not on the correct face, try another one (rotate on edges)
       */
    public DCHalfEdge getPrevEdgeOfFace(DCVertex vertex, DCFace face){

        DCHalfEdge twin = vertex.getLeavingEdge().getTwin();
        DCHalfEdge edge = twin.getNext().getTwin();
        //if there is only one face, "edge" is the previous edge
        while(edge != twin){
            if(edge.getFace() == face)
                return edge;
            edge = edge.getNext().getTwin();//rotate
        }
        //default to twin because if we assume that "face" is one of the adjacent faces, the last choice must be the correct one.
        return twin;
    }

    public DCFace getCommonFace(DCVertex vertex1, DCVertex vertex2){
        if(vertex1.getLeavingEdge().getFace() == vertex2.getLeavingEdge().getFace()){
            return vertex1.getLeavingEdge().getFace();
        }
        DCHalfEdge firstTwin = vertex1.getLeavingEdge().getTwin();
        //we need to test all incoming edges pair and find the pair with matching faces
        DCHalfEdge currEdge1 = firstTwin.getNext().getTwin();
        //start with second twin, end with first (after a completing the loop on each edges)
        while(currEdge1 != firstTwin){
            DCHalfEdge firstTwin2 = vertex2.getLeavingEdge().getTwin();
            DCHalfEdge currEdge2 = firstTwin2.getNext().getTwin();
            while(currEdge2 != firstTwin2){
                if(currEdge1.getFace() == currEdge2.getFace())
                    return currEdge1.getFace();
                currEdge2 = currEdge2.getNext().getTwin();
            }
            currEdge1 = currEdge1.getNext().getTwin();
        }
        return firstTwin.getFace();//TODO check if correct
    }

    //Add an edge between two vertices and update everything.
    public void addEdge(DCVertex vertex1, DCVertex vertex2){
        /*
                New face
          v1 -----topEdge----->
             <----bottomEdge--- v2
                Existing face
        */
        //adding an edge creates a new face:
        DCFace newFace = new DCFace();
        DCFace existingFace = getCommonFace(vertex1, vertex2);

        DCHalfEdge topEdge = new DCHalfEdge();
        DCHalfEdge bottomEdge = new DCHalfEdge();
        newFace.setRefEdge(topEdge);

        DCHalfEdge prev1 = getPrevEdgeOfFace(vertex1, existingFace);
        DCHalfEdge prev2 = getPrevEdgeOfFace(vertex2, existingFace);

        topEdge.setFace(newFace);
        bottomEdge.setFace(existingFace);

        topEdge.setNext(prev2.getNext());
        bottomEdge.setNext(prev1.getNext());

        topEdge.setOrigin(vertex1);
        bottomEdge.setOrigin(vertex2);

        topEdge.setTwin(bottomEdge);
        bottomEdge.setTwin(topEdge);

        prev1.setNext(topEdge);
        prev2.setNext(bottomEdge);

        //now update all half-edges of the new face
        DCHalfEdge currEdge = topEdge.getNext();
        while(currEdge != topEdge){
            currEdge.setFace(newFace);
            currEdge = currEdge.getNext();
        }

        faces.add(newFace);
        edges.add(topEdge);
        edges.add(bottomEdge);
    }

    public void addEdge(int idxVertex1, int idxVertex2){
        DCVertex vertex1 = vertices.get(idxVertex1);
        DCVertex vertex2 = vertices.get(idxVertex2);
        this.addEdge(vertex1, vertex2);
    }

    @Override
    public void draw(DrawContext context) {
        context.applyStyle();
        final var applet = context.applet();


        applet.fill(0,0,255);

        int i =0;
        for (var vertex : vertices) {
            applet.circle(vertex.getPoint().x, vertex.getPoint().y, context.style().getPointSize());
            applet.text(Integer.toString(i), vertex.getPoint().x + 5, vertex.getPoint().y + 5);
            i++;
        }

        for (DCHalfEdge edge : edges) {
            var start = edge.getOrigin().getPoint();
            var end = edge.getDestination().getPoint();
            applet.line(start.x, start.y, end.x, end.y);
        }
    }

    public ArrayList<DCHalfEdge> getEdges() {
        return edges;
    }

    public ArrayList<DCVertex> getVertices() {
        return vertices;
    }

    public ArrayList<DCFace> getFaces() {
        return faces;
    }
}
