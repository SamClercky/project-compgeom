package be.ulbvub.compgeom;

import be.ulbvub.compgeom.utils.Utils;
import processing.core.PVector;

import java.util.function.Consumer;

public class VBox implements Drawable {
    private final DrawContext context;

    private VBox(DrawContext context) {
        this.context = context;
    }

    public static VBox with(DrawContext context) {
        return new VBox(context);
    }

    public VBox draw(Consumer<DrawContext> child) {
        final var claimedX = context.claimedRegion().size().x;
        var unclaimedRegion = Region.emptyRegion()
                .withStart(claimedX, 0)
                .withSize(context.size().copy().sub(new PVector(claimedX, 0)));
        DrawRegion.apply(unclaimedRegion, context.withNoClaim(), (ctx) -> {
            child.accept(ctx);
            final var childClaimed = ctx.claimedRegion();
            context.claimRegion(childClaimed.size().x, 0);
            context.setClaimedRegion(context.claimedRegion().size().x, Utils.max(childClaimed.size().y, context.claimedRegion().size().y));
        });

        return this;
    }
}
