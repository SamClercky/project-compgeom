package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.function.Consumer;

public class TransformGuard implements Drawable {
    private PVector translation;
    private float rotationAngle;
    private PVector scale;

    public TransformGuard() {
        this.translation = new PVector(0.0f, 0.0f);
        this.rotationAngle = 0.0f;
        this.scale = new PVector(1.0f, 1.0f);
    }

    public static void apply(PVector translation, float rotationAngle, PVector scale, DrawContext context, Consumer<DrawContext> child) {
        final var guard = new TransformGuard();
        guard.setTranslation(translation);
        guard.setRotationAngle(rotationAngle);
        guard.setScale(scale);
        guard.draw(context, child);
    }

    public static void apply(PVector translation, DrawContext context, Consumer<DrawContext> child) {
        final var guard = new TransformGuard();
        guard.setTranslation(translation);
        guard.draw(context, child);
    }

    public void setTranslation(PVector translation) {
        this.translation = translation;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public void setScale(PVector scale) {
        this.scale = scale;
    }

    public void applyScale(PVector scale) {
        this.scale.x *= scale.x;
        this.scale.y *= scale.y;
        this.scale.z *= scale.z;
    }

    @Override
    public void draw(DrawContext context) {
        // nop
    }

    @Override
    public void draw(DrawContext context, Consumer<DrawContext> child) {
        final var applet = context.applet();

        // setup transformation
        applet.pushMatrix();
        applet.translate(translation.x, translation.y);
        applet.rotate(rotationAngle, 0.0f, 0.0f, 1.0f);
        applet.scale(scale.x, scale.y, 1.0f);

        // draw further
        child.accept(context);

        // reset transformation
        applet.popMatrix();
    }
}
