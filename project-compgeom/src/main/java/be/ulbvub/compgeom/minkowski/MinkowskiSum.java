package be.ulbvub.compgeom.minkowski;

import be.ulbvub.compgeom.ui.DrawContext;
import be.ulbvub.compgeom.ui.Drawable;
import be.ulbvub.compgeom.utils.*;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MinkowskiSum implements Drawable, CalculationResult {

    ArrayList<DoublyConnectedEdgeList> convoluted = new ArrayList<>();

    /*
    Minkowski sum of convex polygons, based on this article: https://cp-algorithms.com/geometry/minkowski.html
     */

    private static void reorder(ArrayList<DCVertex> vertices){
        int pos = 0;
        for(int i =1; i< vertices.size(); i++){
            PVector lhs = vertices.get(i).getPoint();
            PVector rhs = vertices.get(pos).getPoint();
            if(lhs.y < rhs.y || (lhs.y == rhs.y && lhs.x < rhs.x)){
                pos = i;
            }
        }
        Collections.rotate(vertices, -pos);
    }

    public static DoublyConnectedEdgeList minkowski(DoublyConnectedEdgeList convex1, DoublyConnectedEdgeList convex2) {
        ArrayList<DCVertex> vertices1 = (ArrayList<DCVertex>) convex1.getVertices().clone();
        ArrayList<DCVertex> vertices2 = (ArrayList<DCVertex>) convex2.getVertices().clone();
        return minkowski(vertices1, vertices2);
    }

    public static DoublyConnectedEdgeList minkowski(ArrayList<DCVertex> vertices1, ArrayList<DCVertex> vertices2) {

        reorder(vertices1);
        reorder(vertices2);

        int n1 = vertices1.size();
        int n2 = vertices2.size();
        int i = 0,j = 0;
        ArrayList<PVector> result = new ArrayList<>();
        while(i < n1 || j < n2){
            PVector point1 = vertices1.get(i%n1).getPoint();
            PVector point2 = vertices2.get(j%n2).getPoint();
            PVector sumPoint = PVector.add(point1, point2);
            result.add(sumPoint);
            PVector edge1 = PVector.sub(vertices1.get((i+1)%n1).getPoint(), point1);
            PVector edge2 = PVector.sub(vertices2.get((j+1)%n2).getPoint(), point2);
            float crossProduct = TurnDirection.orientationRaw(new PVector(0,0,0),edge2, PVector.add(edge1,edge2));
            if(crossProduct >= 0 && i < n1){
                ++i;
            }
            if(crossProduct <= 0 && j < n2){
                ++j;
            }
        }
        return new DoublyConnectedEdgeList(result);
    }

    public MinkowskiSum(DoublyConnectedEdgeList decomposition, DoublyConnectedEdgeList convexShape){
        ArrayList<DCVertex> verticesConvex = (ArrayList<DCVertex>) convexShape.getVertices().clone();
        for(DCFace face : decomposition.getFaces()){
            ArrayList<DCVertex> verticesFace = decomposition.getVerticesOfFace(face);
            convoluted.add(minkowski(verticesFace, verticesConvex));
        }
    }

    @Override
    public void draw(DrawContext context) {
        context.applyStyle();
        final var applet = context.applet();

        Random rand = new Random();
        int[] color = {50, 200, 50};
        for (DoublyConnectedEdgeList edgeList : convoluted) {
            DCFace face = edgeList.getFaces().get(0);//should have on and only one face
            rand.setSeed(face.hashCode());
            applet.fill(color[0], color[1], color[2],100f);
            applet.beginShape();
            for (DCVertex vertex : edgeList.getVerticesOfFace(face)) {
                float x = vertex.getPoint().x;
                float y = vertex.getPoint().y;
                applet.vertex(x, y);
            }
            applet.endShape(PConstants.CLOSE);
        }
    }


    @Override
    public int getFaceCount() {
        int count = 0;
        for (var dcel: convoluted) {
            count += dcel.getFaceCount();
        }
        return count;
    }

    @Override
    public int getVertexCount() {
        int count = 0;
        for (var dcel: convoluted) {
            count += dcel.getVertexCount();
        }
        return count;
    }

    @Override
    public int getHalfEdgeCount() {
        int count = 0;
        for (var dcel: convoluted) {
            count += dcel.getHalfEdgeCount();
        }
        return count;
    }
}
