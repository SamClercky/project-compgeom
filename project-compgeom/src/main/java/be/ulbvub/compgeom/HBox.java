package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.function.Consumer;

public class HBox implements Drawable {
    private final DrawContext context;

    private HBox(DrawContext context) {
        this.context = context;
    }

    public static HBox with(DrawContext context) {
        return new HBox(context);
    }

    public HBox draw(Consumer<DrawContext> child) {
        final var claimedY = context.claimedRegion().size().y;
        var unclaimedRegion = Region.emptyRegion()
                .withStart(0, claimedY)
                .withSize(context.size().copy().sub(new PVector(0, claimedY)));
        DrawRegion.apply(unclaimedRegion, context.withNoClaim(), (ctx) -> {
            child.accept(ctx);
            final var childClaimed = ctx.claimedRegion();
            context.claimRegion(0, childClaimed.size().y);
        });
        context.setClaimedRegion(context.size().x, context.claimedRegion().size().y);

        return this;
    }
}
