package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.Utils;

import java.util.function.Consumer;

public class StackBox implements Drawable {
    private final DrawContext context;

    private StackBox(DrawContext context) {
        this.context = context;
    }

    public static StackBox with(DrawContext context) {
        return new StackBox(context);
    }

    public StackBox draw(Consumer<DrawContext> child) {
        DrawRegion.apply(context.region().withStart(0, 0), context.withNoClaim(), (ctx) -> {
            child.accept(ctx);
            final var childClaimed = ctx.claimedRegion();
            context.setClaimedRegion(
                    Utils.max(childClaimed.size().y, context.claimedRegion().size().x),
                    Utils.max(childClaimed.size().y, context.claimedRegion().size().y));
        });

        return this;
    }
}
