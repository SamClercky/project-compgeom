package be.ulbvub.compgeom;

import java.util.function.Function;

public interface Drawable {
    default void draw(DrawContext context) {
    }

    default void draw(DrawContext context, Function<DrawContext, Void> child) {
        draw(context);
        child.apply(context);
    }
}
