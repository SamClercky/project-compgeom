package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.function.Function;

public class DrawRegion implements Drawable {
    private final PVector size;
    private final TransformGuard guard;
    private final PVector start;

    public DrawRegion(PVector start, PVector size) {
        this.start = start;
        this.size = size;
        guard = new TransformGuard();
        guard.setTranslation(start);
    }

    public static void apply(PVector start, PVector size, DrawContext context, Function<DrawContext, Void> child) {
        final var region = new DrawRegion(start, size);
        region.draw(context, child);
    }

    @Override
    public void draw(DrawContext context) {
        guard.draw(context);
    }

    @Override
    public void draw(DrawContext context, Function<DrawContext, Void> child) {
        guard.draw(context, (ctx) -> {
            var newContext = context.withStart(start).withSize(size);
            if (newContext.mouseClicked() != null && !newContext.isInside(newContext.mouseClicked().position())) {
                newContext = newContext.consumeEvent(start);
            }
            child.apply(newContext);
            return null;
        });
    }
}
