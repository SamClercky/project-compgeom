package be.ulbvub.compgeom.kd;

import be.ulbvub.compgeom.utils.DCVertex;

import java.util.Comparator;

public class DCVertexXComparator implements Comparator<DCVertex> {
    @Override
    public int compare(DCVertex o1, DCVertex o2) {
        return Float.compare(o1.getPoint().x, o2.getPoint().x);
    }
}
