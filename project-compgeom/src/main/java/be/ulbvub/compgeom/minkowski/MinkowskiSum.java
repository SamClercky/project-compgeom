package be.ulbvub.compgeom.minkowski;

import be.ulbvub.compgeom.utils.DCVertex;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import be.ulbvub.compgeom.utils.TurnDirection;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;

public class MinkowskiSum {

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
        Collections.rotate(vertices, pos);
    }

    public static DoublyConnectedEdgeList minkowski(DoublyConnectedEdgeList convex1, DoublyConnectedEdgeList convex2){

        ArrayList<DCVertex> vertices1 = (ArrayList<DCVertex>) convex1.getVertices().clone();
        reorder(vertices1);

        ArrayList<DCVertex> vertices2 = (ArrayList<DCVertex>) convex2.getVertices().clone();
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
            TurnDirection crossProduct = TurnDirection.orientation(edge1, new PVector(0,0,0), edge2);
            if(crossProduct != TurnDirection.LEFT && i < n1){
                ++i;
            }
            if(crossProduct != TurnDirection.RIGHT && j < n2){
                ++j;
            }
        }
        return new DoublyConnectedEdgeList(result);
    }


}
