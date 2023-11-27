package be.ulbvub.compgeom.utils;

import be.ulbvub.compgeom.ui.Drawable;

public interface CalculationResult extends Drawable {

    int getFaceCount();
    int getVertexCount();
    int getHalfEdgeCount();
}
