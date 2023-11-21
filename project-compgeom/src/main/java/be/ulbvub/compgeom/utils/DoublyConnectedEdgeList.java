package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.triangles.TriangleDecomposition;
import be.ulbvub.compgeom.ui.DrawContext;
import be.ulbvub.compgeom.ui.Drawable;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.*;

public class DoublyConnectedEdgeList implements Drawable {

    ArrayList<DCHalfEdge> edges = new ArrayList<DCHalfEdge>();
    ArrayList<DCVertex> vertices = new ArrayList<DCVertex>();
    ArrayList<DCFace> faces = new ArrayList<DCFace>();


    HashMap<DCVertex,DCFace> reflexVertices = new HashMap<>();

    public DoublyConnectedEdgeList(Polygon polygon) {
        this(polygon.points());
    }



    //take as argument a list of connected points
    public DoublyConnectedEdgeList(ArrayList<PVector> points) {

        Polygon p = new Polygon(points);
        //get points in counter-clockwise order
        Iterator<PVector> iter = p.ccwIterator();
        ArrayList<PVector> ccwPoints = new ArrayList<>();
        assert iter != null;
        while (iter.hasNext()) {
            ccwPoints.add(iter.next());
        }

        if (!ccwPoints.isEmpty()) {
            DCFace face = new DCFace();
            faces.add(face);
            DCHalfEdge prevEdge = null;
            DCHalfEdge prevTwin = null;

            for (PVector point : ccwPoints) {
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

                if (prevEdge != null)
                    prevEdge.setNext(currEdge);

                if (prevTwin != null)
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

        for(DCVertex vertex : vertices){
            PVector prev = vertex.getLeavingEdge().getTwin().getNext().getDestination().getPoint();
            PVector next = vertex.getLeavingEdge().getDestination().getPoint();
            TurnDirection dir = TurnDirection.orientation(prev, vertex.getPoint(), next);
            if(dir != TurnDirection.LEFT){//reflex angle
                reflexVertices.put(vertex, vertex.getLeavingEdge().getFace());
            }
        }

    }

    /*
      How it works :
      Try on incoming edge, if it is not on the correct face, try another one (rotate on edges)
       */
    public static DCHalfEdge getPrevEdgeOfFace(DCVertex vertex, DCFace face) {

        DCHalfEdge twin = vertex.getLeavingEdge().getTwin();
        DCHalfEdge edge = twin.getNext().getTwin();
        //if there is only one face, "edge" is the previous edge
        while (edge != twin) {
            if (edge.getFace() == face)
                return edge;
            edge = edge.getNext().getTwin();//rotate
        }
        //default to twin because if we assume that "face" is one of the adjacent faces, the last choice must be the correct one.
        return twin;
    }

    public DCFace getCommonFace(DCVertex vertex1, DCVertex vertex2) {
        if (vertex1.getLeavingEdge().getFace() == vertex2.getLeavingEdge().getFace()) {
            return vertex1.getLeavingEdge().getFace();
        }
        DCHalfEdge firstTwin = vertex1.getLeavingEdge().getTwin();
        //we need to test all incoming edges pair and find the pair with matching faces
        DCHalfEdge currEdge1 = firstTwin.getNext().getTwin();
        //start with second twin, end with first (after a completing the loop on each edges)
        while (currEdge1 != firstTwin) {
            DCHalfEdge firstTwin2 = vertex2.getLeavingEdge().getTwin();
            DCHalfEdge currEdge2 = firstTwin2.getNext().getTwin();
            while (currEdge2 != firstTwin2) {
                if (currEdge1.getFace() == currEdge2.getFace())
                    return currEdge1.getFace();
                currEdge2 = currEdge2.getNext().getTwin();
            }
            currEdge1 = currEdge1.getNext().getTwin();
        }
        return firstTwin.getFace();//TODO check if correct
    }

    /**
     * Inserts a vertex at the given position on the given half edge.
     * Assumption is made that adding the vertex, the graph that is
     * being represented by the DCEL still remains a planar graph and
     * no overlapping is happening.
     * <p>
     * Go from:
     * <pre>
     * <--- edge ---
     * --- twin --->
     * </pre>
     * to:
     * <pre>
     * <--- exEdge --- vertex <--- edge   ---
     * --- twin   ---> vertex ---> exTwin ---
     * </pre>
     *
     * @param edge     The edge on which to insert the new vertex. Should already be present in the DCEL
     * @param position The position the vertex is inserted at
     * @return Reference to created vertex
     */
    public DCVertex addVertex(DCHalfEdge edge, PVector position) {
        // Assert some invariants
        Objects.requireNonNull(edge.getTwin(), "Edge should have a twin");
        Objects.requireNonNull(edge.getOrigin(), "Edge should be part of a DCEL");
        Objects.requireNonNull(edge.getTwin().getOrigin(), "Twin should be part of a DCEL");

        final var vertex = new DCVertex(position);
        final var twin = edge.getTwin();

        // Define the extensions
        final var extendedTwin = new DCHalfEdge(vertex);
        extendedTwin.setTwin(edge);
        extendedTwin.setFace(twin.getFace());
        extendedTwin.setNext(twin.getNext());

        final var extendedEdge = new DCHalfEdge(vertex);
        extendedEdge.setTwin(twin);
        extendedEdge.setFace(edge.getFace());
        extendedEdge.setNext(edge.getNext());

        // Update edge and twin to finalize the link
        edge.setNext(extendedEdge);
        edge.setTwin(extendedTwin);

        twin.setNext(extendedTwin);
        twin.setTwin(extendedEdge);

        vertex.setLeavingEdge(extendedEdge);
        vertices.add(vertex);
        edges.add(extendedEdge);
        edges.add(extendedTwin);

        return vertex;
    }

    public ArrayList<DCVertex> getVerticesOfFace(DCFace face) {
        ArrayList<DCVertex> points = new ArrayList<>();
        DCHalfEdge refEdge = face.getRefEdge();
        DCHalfEdge currEdge = refEdge;
        do {
            DCVertex currVertex = currEdge.getOrigin();
            points.add(currVertex);
            currEdge = currEdge.getNext();
        } while (currEdge != refEdge);
        return points;
    }


    //Add an edge between two vertices and update everything.
    public boolean addEdge(DCVertex vertex1, DCVertex vertex2) {
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

        newFace.setRefEdge(topEdge);
        existingFace.setRefEdge(bottomEdge);

        //now update all half-edges of the new face
        DCHalfEdge currEdge = topEdge.getNext();
        while (currEdge != topEdge) {
            currEdge.setFace(newFace);
            currEdge = currEdge.getNext();
        }

        faces.add(newFace);
        edges.add(topEdge);
        edges.add(bottomEdge);


        //update reflexVertices if needed

        if(reflexVertices.get(vertex1) != null) {
            PVector prev = getPrevEdgeOfFace(vertex1, newFace).getOrigin().getPoint();
            PVector next = vertex2.getPoint();
            TurnDirection dir = TurnDirection.orientation(prev, vertex1.getPoint(), next);
            if (dir != TurnDirection.LEFT) {//reflex angle
                reflexVertices.put(vertex1, newFace);
            }else{
                reflexVertices.remove(vertex1);
            }
            prev = vertex2.getPoint();
            next = bottomEdge.getNext().getDestination().getPoint();
            dir = TurnDirection.orientation(prev, vertex1.getPoint(), next);
            if (dir != TurnDirection.LEFT) {//reflex angle
                reflexVertices.put(vertex1, existingFace);
            }else{
                reflexVertices.remove(vertex1);
            }
        }

        if(reflexVertices.get(vertex2) != null) {
            PVector prev = vertex1.getPoint();
            PVector next = topEdge.getNext().getDestination().getPoint();
            TurnDirection dir = TurnDirection.orientation(prev, vertex2.getPoint(), next);
            if (dir != TurnDirection.LEFT) {//reflex angle
                reflexVertices.put(vertex2, newFace);
            }else{
                reflexVertices.remove(vertex2);
            }
            prev = getPrevEdgeOfFace(vertex2, existingFace).getOrigin().getPoint();
            next = vertex1.getPoint();
            dir = TurnDirection.orientation(prev, vertex2.getPoint(), next);
            if (dir != TurnDirection.LEFT) {//reflex angle
                reflexVertices.put(vertex2, existingFace);
            }else{
                reflexVertices.remove(vertex2);
            }
        }
        return reflexVertices.isEmpty();
    }

    public void addEdge(int idxVertex1, int idxVertex2) {
        DCVertex vertex1 = vertices.get(idxVertex1);
        DCVertex vertex2 = vertices.get(idxVertex2);
        this.addEdge(vertex1, vertex2);
    }

    @Override
    public void draw(DrawContext context) {
        context.applyStyle();
        final var applet = context.applet();


        Random rand = new Random();
        for (DCFace face : faces) {

            rand.setSeed(face.hashCode());
            applet.fill(rand.nextInt(50, 200), rand.nextInt(50, 200), rand.nextInt(50, 200));
            applet.beginShape();
            int i = 0;
            for (DCVertex vertex : this.getVerticesOfFace(face)) {
                float x = vertex.getPoint().x;
                float y = vertex.getPoint().y;
                applet.vertex(x, y);
                i++;
            }
            applet.endShape(PConstants.CLOSE);
        }

        for (DCHalfEdge edge : edges) {
            var start = edge.getOrigin().getPoint();
            var end = edge.getDestination().getPoint();
            applet.line(start.x, start.y, end.x, end.y);
        }
        for (DCVertex vertex : vertices) {
            applet.circle(vertex.getPoint().x, vertex.getPoint().y, context.style().getPointSize());
        }
    }

    /**
     * Query an edge from which a certain line is correlated
     *
     * @param line The line to query
     * @return The first half edge that is correlated to the provided line
     */
    public DCHalfEdge getEdgeByLine(Line line) {
        for (var edge : edges) {
            if (line.equals(edge.toLine()))
                return edge;
        }
        return null;
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

    public boolean hasReflex(){
        return !reflexVertices.isEmpty();
    }
}
