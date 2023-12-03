package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.minkowski.MinkowskiSum;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;

public record MinkowskiConfig(Polygon a, Polygon b, DecompositionConfig dConfig) {

    public MinkowskiSum calculate() {
        dConfig.setPolygon(a);
        final var dA = dConfig.decompose();
        final var dB = new DoublyConnectedEdgeList(b); // Shape B is assumed to be always convex

        if (dA instanceof DoublyConnectedEdgeList dcel) {
            return new MinkowskiSum(dcel, dB);
        } else if (dA instanceof PolygonGroup pg) {
            return new MinkowskiSum(pg, dB);
        } else {
            throw new RuntimeException("Not supported decomposition result");
        }
    }
}
