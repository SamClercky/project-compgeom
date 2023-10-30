package be.ulbvub.compgeom.triangles;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Stack;

import static be.ulbvub.compgeom.utils.TurnDirection.*;

public class TriangleDecomposition {

    // Return True for one side and false for the other.
    // Note that we don't need to know which one is left/right, and that it depends on the side of the first element.
    private static boolean sideOf(int index, int topIdx, int bottomIdx){
        return index > topIdx == index > bottomIdx;
    }

    private static boolean canSee(int vertexBottom, int vertexTop, ArrayList<PVector> points){
        int n = points.size();
        PVector bottomVec = points.get(vertexBottom);
        PVector topVec = points.get(vertexTop);
        boolean itCan = true;
        if(vertexBottom < vertexTop){
            int vertexInter = vertexBottom + 1;
            while(itCan && vertexInter != vertexTop){
                itCan = orientation(bottomVec, points.get(vertexInter), topVec) == LEFT;
                vertexInter ++;
            }
        }else{
            int vertexInter = (vertexBottom + n - 1) % n;// vertexBottom - 1 (mod) n
            while(itCan && vertexInter != vertexTop){
                itCan = orientation(bottomVec, points.get(vertexInter), topVec) == RIGHT;
                vertexInter = (vertexInter + n - 1) % n;
            }
        }
        System.out.println("CanSee: " + Boolean.toString(itCan) + " bottom: " + Integer.toString(vertexBottom) + " top:" + Integer.toString(vertexTop));
        return itCan;
    }

    public static DoublyConnectedEdgeList triangulateYMonotonePolygon(Polygon p){
        //TODO check for direction
        int topVertexIdx = p.getLeftMostAlongDirection(new PVector(1, 0));
        ArrayList<Integer> order = new ArrayList<>();
        ArrayList<PVector> points = p.getPoints();
        int n = p.getPoints().size();
        int currLeft = topVertexIdx;
        int currRight = (topVertexIdx + (n - 1)) % n;// idx - 1 (mod) n;
        float currLeftY = points.get(currLeft).y;
        float currRightY = points.get(currRight).y;
        while(currLeft != currRight){
            if(currLeftY > currRightY){
                order.add(currLeft);
                currLeft = (currLeft + 1) % n;
                currLeftY = points.get(currLeft).y;
            }else{
                order.add(currRight);
                currRight = (currRight + (n - 1)) % n;// idx - 1 (mod) n;
                currRightY = points.get(currRight).y;
            }
        }
        order.add(currLeft);
        int bottomVertexIdx = currRight;

        DoublyConnectedEdgeList dcEdgeList = new DoublyConnectedEdgeList(points);

        Stack<Integer> stack = new Stack<>();
        stack.push(order.get(0));
        stack.push(order.get(1));

        for(int j =2; j < n - 1; j++){
            int uj = order.get(j);
            if(sideOf(uj, topVertexIdx, bottomVertexIdx) != sideOf(stack.peek(), topVertexIdx, bottomVertexIdx)){
                while(!stack.empty()){
                    int v = stack.pop();
                    if(!stack.empty())dcEdgeList.addEdge(uj, v);
                }
                stack.push(order.get(j-1));
                stack.push(order.get(j));
            }else{
                int v = stack.pop();
                while(!stack.empty() && canSee(uj, stack.peek(), points)){
                    v = stack.pop();
                    dcEdgeList.addEdge(uj, v);
                }
                stack.push(v);
                stack.push(order.get(j));
            }
        }

        return dcEdgeList;
    }
}
