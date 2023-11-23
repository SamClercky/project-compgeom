package be.ulbvub.compgeom.kd;

import java.util.Comparator;

public class DCVertexXComparator implements Comparator<KdVertex> {
    @Override
    public int compare(KdVertex o1, KdVertex o2) {
        return Float.compare(o1.vertex().getPoint().x, o2.vertex().getPoint().x);
    }
}
