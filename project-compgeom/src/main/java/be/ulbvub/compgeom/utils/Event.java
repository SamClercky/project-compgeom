package be.ulbvub.compgeom.utils;

import processing.core.PVector;

public interface Event<T> {

    PVector getPoint();

    T getReason();
}
