package be.ulbvub.compgeom.kd;

import java.util.Comparator;

public class DCVertexYComparator implements Comparator<KdVertex> {
    @Override
    public int compare(KdVertex o1, KdVertex o2) {
        return Float.compare(o1.vertex().getPoint().y, o2.vertex().getPoint().y);
    }
}
