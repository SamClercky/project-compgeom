package be.ulbvub.compgeom.ui;

import be.ulbvub.compgeom.Polygon;
import be.ulbvub.compgeom.minkowski.MinkowskiSum;
import be.ulbvub.compgeom.utils.DoublyConnectedEdgeList;

public record MinkowskiConfig(Polygon a, Polygon b, DecompositionConfig dConfig) {

    public DoublyConnectedEdgeList calculate() {
        dConfig.setPolygon(a);
        final var dA = dConfig.decompose();
        dConfig.setPolygon(b);
        final var dB = dConfig.decompose();

        return MinkowskiSum.minkowski(dA, dB);
    }
}
