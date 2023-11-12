package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public interface Event<T> {

    default PVector getPoint() {
        return getVertex().getPoint();
    }

    DCVertex getVertex();

    T getReason();
}
