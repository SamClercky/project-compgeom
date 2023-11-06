package be.ulbvub.compgeom.ui;

import java.util.function.Consumer;

public interface Drawable {
    default void draw(DrawContext context) {
    }

    default void draw(DrawContext context, Consumer<DrawContext> child) {
        draw(context);
        child.accept(context);
    }
}
