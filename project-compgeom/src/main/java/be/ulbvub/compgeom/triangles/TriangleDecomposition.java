package be.ulbvub.compgeom.triangles;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.*;
import processing.core.PVector;

import java.util.*;

import static be.ulbvub.compgeom.utils.DoublyConnectedEdgeList.getPrevEdgeOfFace;
import static be.ulbvub.compgeom.utils.TurnDirection.*;

public class TriangleDecomposition {

    // Return True is point is on left side
    private static boolean sideOf(int index, int topIdx, int bottomIdx) {
        boolean isOnJunctionSide = (index > topIdx == index > bottomIdx);
        if(topIdx > bottomIdx)//junction side is left (ccw order)
            return isOnJunctionSide;
        else
            return !isOnJunctionSide;
    }

    private static boolean canSee(int vertexBottom, int vertexInter, int vertexTop, ArrayList<DCVertex> points, int topIdx, int bottomIdx) {
        PVector bottomVec = points.get(vertexBottom).getPoint();
        PVector topVec = points.get(vertexTop).getPoint();
        TurnDirection orientation = orientation(bottomVec, points.get(vertexInter).getPoint(), topVec);
        return (orientation == STRAIGHT) || ((orientation == RIGHT) == sideOf(vertexBottom, topIdx, bottomIdx));
    }


    public enum VertexType {
        START,
        SPLIT,
        END,
        MERGE,
        REGULAR
    }

    /**
     * Returns true if point a is below point b.
     * If the point a.y and b.y are equals compare their x value
     */
    public static boolean isBelow(PVector a, PVector b) {
        double diff = a.y - b.y;
        if (diff == 0.0) {
            return a.x > b.x;
        } else {
            return !(diff < 0.0);
        }
    }

    private static VertexType getType(DCVertex vertex) {

        DCHalfEdge prev = vertex.getLeavingEdge().getTwin().getNext().getTwin();
        DCHalfEdge next = vertex.getLeavingEdge();

        PVector a = prev.getOrigin().getPoint();
        PVector b = vertex.getPoint();
        PVector c = next.getDestination().getPoint();
        boolean cIsBelow = isBelow(c, b);
        boolean aIsBelow = isBelow(a, b);

        TurnDirection turn = orientation(a,b,c);
        HalfEdgeComparator comparator = new HalfEdgeComparator();
        System.out.println("Cross: " + orientation(a,b,c));
        System.out.println("Comparator: " + comparator.compare(prev, next));
        if (!cIsBelow && !aIsBelow) {
            if (turn == TurnDirection.LEFT) {
                //inside is up
                return VertexType.END;
            } else {
                return VertexType.MERGE;
            }
        } else if (cIsBelow && aIsBelow) {
            if (turn == TurnDirection.LEFT) {
                //inside is up
                return VertexType.START;
            } else {
                return VertexType.SPLIT;
            }
        }

        return VertexType.REGULAR;
    }

    public static DoublyConnectedEdgeList triangulatePolygon(Polygon p){
        //first split into y-monotone polygons
        DoublyConnectedEdgeList dcEdgeList = splitMonotone(p);
        System.out.println("Number of monotone polygons:" + dcEdgeList.getFaces().size());
        for(DCFace face : (ArrayList<DCFace>) dcEdgeList.getFaces().clone()){//copy list because triangulation modify it
            triangulateYMonotonePolygon(dcEdgeList, face);
        }
        return dcEdgeList;
    }


    public static DoublyConnectedEdgeList splitMonotone(Polygon p) {

        System.out.println("Split monotone:");
        //get points in counter-clockwise order
        Iterator<PVector> iter = p.ccwIterator();
        ArrayList<PVector> points = new ArrayList<>();
        assert iter != null;
        while (iter.hasNext()) {
            points.add(iter.next());
        }
        //System.out.println("Points:" + points);
        DoublyConnectedEdgeList dcEdgeList = new DoublyConnectedEdgeList(points);

        ArrayList<DCVertex> vertices = (ArrayList<DCVertex>) dcEdgeList.getVertices().clone();
        vertices.sort((left, right) -> {
            PVector lhs = left.getPoint();
            PVector rhs = right.getPoint();

            if (lhs.y > rhs.y || (lhs.y == rhs.y && lhs.x > rhs.x)) {
                return 1;
            } else if (lhs.y < rhs.y || (lhs.y == rhs.y && lhs.x < rhs.x)) {
                return -1;
            }
            return 0;
        });


        HashMap<DCVertex,VertexType> types = new HashMap<>();
        for(DCVertex vertex : vertices){
            types.put(vertex, getType(vertex));
        }

        TreeSet<DCHalfEdge> edgeTree = new TreeSet<>(new HalfEdgeComparator());
        TreeMap<DCHalfEdge, VertexAndType> helperMap = new TreeMap<>(new HalfEdgeComparator());

        for (DCVertex vertex : vertices) {
            VertexType type = types.get(vertex);
            System.out.println("Type: " + type.toString() + " point:" + vertex.getPoint());
            DCHalfEdge prevEdge = getPrevEdgeOfFace(vertex, vertex.getLeavingEdge().getFace());
            DCHalfEdge nextEdge = vertex.getLeavingEdge();
            switch (type) {
                case START: {
                    edgeTree.add(nextEdge);
                    helperMap.put(nextEdge, new VertexAndType(vertex, type));

                    System.out.println(prevEdge + " " + nextEdge);
                    break;
                }
                case SPLIT: {

                    //get left edge and connect "vertex" to his helper
                    DCHalfEdge leftOfVertex = edgeTree.floor(nextEdge);
                    if (leftOfVertex == null) throw new IllegalArgumentException("It must be a simple polygon");
                    dcEdgeList.addEdge(vertex, helperMap.get(leftOfVertex).getVertex());

                    //update the helper for left edge
                    helperMap.put(leftOfVertex, new VertexAndType(vertex, type));

                    //add edges connected to vertex
                    edgeTree.add(nextEdge);
                    helperMap.put(nextEdge, new VertexAndType(vertex, type));

                    System.out.println(prevEdge + " " + nextEdge + " " + leftOfVertex);
                    break;
                }
                case MERGE: {
                    if (helperMap.get(prevEdge).getType() == VertexType.MERGE) {
                        dcEdgeList.addEdge(vertex, helperMap.get(prevEdge).getVertex());
                    }
                    edgeTree.remove(prevEdge);
                    helperMap.remove(prevEdge);


                    //get edge to left of vertex using "edge" to compare
                    DCHalfEdge leftOfVertex = edgeTree.floor(prevEdge);
                    if (leftOfVertex == null) throw new IllegalArgumentException("It must be a simple polygon");

                    if (helperMap.get(leftOfVertex).getType() == VertexType.MERGE) {
                        dcEdgeList.addEdge(vertex, helperMap.get(leftOfVertex).getVertex());
                    }
                    helperMap.put(leftOfVertex, new VertexAndType(vertex, type));


                    System.out.println(leftOfVertex);
                    break;
                }
                case END: {

                    System.out.println(prevEdge);
                    //remove old edge
                    VertexAndType helper = helperMap.get(prevEdge);
                    //System.out.println("Helper type:" + helper.getType().toString());
                    if (helper.getType() == VertexType.MERGE) {
                        dcEdgeList.addEdge(vertex, helper.getVertex());

                    }
                    helperMap.remove(prevEdge);
                    edgeTree.remove(prevEdge);


                    break;
                }
                case REGULAR: {

                    VertexAndType helper = helperMap.get(prevEdge);

                    if (helper != null) {//edge on left side, TODO test if always true
                        //check for merge and remove old edge
                        if (helper.getType() == VertexType.MERGE) {
                            dcEdgeList.addEdge(vertex, helper.getVertex());

                        }
                        helperMap.remove(prevEdge);
                        edgeTree.remove(prevEdge);
                        //add new edge
                        edgeTree.add(nextEdge);
                        helperMap.put(nextEdge, new VertexAndType(vertex, type));

                    }else{//edge on right side
                        //get edge to left of vertex using "edge" to compare
                        DCHalfEdge leftOfVertex = edgeTree.floor(prevEdge);
                        if (leftOfVertex == null) throw new IllegalArgumentException("It must be a simple polygon");
                        helper = helperMap.get(leftOfVertex);
                        if (helper.getType() == VertexType.MERGE) {
                            dcEdgeList.addEdge(vertex, helper.getVertex());
                        }
                        helperMap.put(leftOfVertex, new VertexAndType(vertex, type));
                    }

                    //System.out.println(edgeAbove + " " + nextEdge);
                    break;
                }
            }
        }


        return dcEdgeList;
    }




    public static DoublyConnectedEdgeList triangulateYMonotonePolygon(Polygon p) {

        //get points in counter-clockwise order
        Iterator<PVector> iter = p.ccwIterator();
        ArrayList<PVector> points = new ArrayList<>();
        assert iter != null;
        while (iter.hasNext()) {
            points.add(iter.next());
        }

        DoublyConnectedEdgeList dcEdgeList = new DoublyConnectedEdgeList(points);
        triangulateYMonotonePolygon(dcEdgeList, dcEdgeList.getFaces().get(0));
        return dcEdgeList;
    }

    public static void triangulateYMonotonePolygon(DoublyConnectedEdgeList dcEdgeList, DCFace face){
        //System.out.println("Triangulate face: " + face.toString());
        ArrayList<DCVertex> points = new ArrayList<>();
        DCHalfEdge refEdge = face.getRefEdge();
        DCHalfEdge currEdge = refEdge;
        int topVertexIdx = -1;
        float topVertexY = 0;
        int idx = 0;
        do{
            DCVertex currVertex = currEdge.getOrigin();
            if(topVertexIdx == -1 || topVertexY > currVertex.getPoint().y){
                topVertexY = currVertex.getPoint().y;
                topVertexIdx = idx;
            }
            points.add(currVertex);
            currEdge = currEdge.getNext();
            idx ++;
        }while (currEdge != refEdge);

        //System.out.println("Points: " + points);

        ArrayList<Integer> order = new ArrayList<>();
        int n = points.size();

        int currLeft = topVertexIdx;
        int currRight = (topVertexIdx + (n - 1)) % n;// idx - 1 (mod) n;
        float currLeftY = points.get(currLeft).getPoint().y;
        float currRightY = points.get(currRight).getPoint().y;
        while (currLeft != currRight) {
            if (currLeftY < currRightY) {
                order.add(currLeft);
                currLeft = (currLeft + 1) % n;
                currLeftY = points.get(currLeft).getPoint().y;
            } else {
                order.add(currRight);
                currRight = (currRight + (n - 1)) % n;// idx - 1 (mod) n;
                currRightY = points.get(currRight).getPoint().y;
            }
        }
        order.add(currLeft);
        int bottomVertexIdx = currLeft;
        System.out.println("Order: " + order);

        Stack<Integer> stack = new Stack<>();
        stack.push(order.get(0));
        stack.push(order.get(1));

        for (int j = 2; j < n - 1; j++) {
            System.out.println("Stack: " + stack);
            int uj = order.get(j);
            if (sideOf(uj, topVertexIdx, bottomVertexIdx) != sideOf(stack.peek(), topVertexIdx, bottomVertexIdx)) {
                System.out.println("Different side");
                while (!stack.empty()) {
                    int v = stack.pop();
                    if (!stack.empty()) {
                        dcEdgeList.addEdge(points.get(uj), points.get(v));
                        System.out.println("Add edge");
                    }
                }
                stack.push(order.get(j - 1));
                stack.push(uj);
            } else {
                int v = stack.pop();
                while (!stack.empty() && canSee(uj, v, stack.peek(), points, topVertexIdx, bottomVertexIdx)) {
                    v = stack.pop();
                    dcEdgeList.addEdge(points.get(uj), points.get(v));
                    System.out.println("Add edge");
                }
                stack.push(v);
                stack.push(uj);
            }
        }

        //draw edge from last vertex to every vertex remaining except his neighbours (top and bottom of stack):

        int un = order.get(n-1);
        System.out.println("Stack: " + stack + " un:"+ un);
        stack.pop();
        while(stack.size() > 1){
            dcEdgeList.addEdge(points.get(un), points.get(stack.pop()));
        }
    }


    public static class VertexAndType {

        DCVertex vertex;
        TriangleDecomposition.VertexType type;
        public VertexAndType(DCVertex vertex, TriangleDecomposition.VertexType type){
            this.vertex = vertex;
            this.type = type;
        }
        public DCVertex getVertex() {
            return vertex;
        }

        public TriangleDecomposition.VertexType getType() {
            return type;
        }

    }

    private static class HalfEdgeComparator implements Comparator<DCHalfEdge> {
        @Override
        public int compare(DCHalfEdge edge1, DCHalfEdge edge2) {
            // we assume here to only deal with simple polygons, so never crossings.
            // otherwise we cannot define a simple global ordering, which is needed here

            if(edge1.equals(edge2))return 0;

            PVector c = edge1.getDestination().getPoint();
            PVector e = edge2.getDestination().getPoint();

            PVector d = edge1.getOrigin().getPoint();
            PVector f = edge2.getOrigin().getPoint();

            //checks to avoid singularities like perpendicular lines
            if(Math.max(c.x, d.x) <= Math.min(e.x, f.x))
                return -1;
            if(Math.min(c.x, d.x) >= Math.max(e.x, f.x))
                return 1;



            ArrayList<Float> xValues = new ArrayList<>(){
                {
                    add(c.x);
                    add(d.x);
                    add(e.x);
                    add(f.x);
                }
            };
            ArrayList<Float> yValues = new ArrayList<>(){
                {
                add(c.y);
                add(d.y);
                add(e.y);
                add(f.y);
                }
            };
            float max = Collections.max(yValues);
            float min = Collections.min(yValues);

            //get line between the two central values
            float cutLineY = (c.y + d.y + e.y + f.y - max - min)/2;
            float minX = Collections.min(xValues);
            PVector a = new PVector(minX-10, cutLineY);

            TurnDirection leftefc = orientation(e,f,c);
            TurnDirection leftefa = orientation(e,f,a);
            TurnDirection leftefd = orientation(e,f,d);

            if((leftefc == STRAIGHT || leftefc == leftefa) &&
               (leftefd == STRAIGHT  || leftefd == leftefa))
                return -1;

            TurnDirection leftcde = orientation(c,d,e);
            TurnDirection leftcda = orientation(c,d,a);
            TurnDirection leftcdf = orientation(c,d,f);


            if (leftcde != leftcda && leftcdf != leftcda) {
                return -1;
            } else  {
                return 1;
            }
        }
    }

}
