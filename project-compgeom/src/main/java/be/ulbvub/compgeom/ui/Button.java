package be.ulbvub.compgeom.ui;

import processing.core.PVector;

import java.util.function.Consumer;
import java.util.function.Function;

public class Button implements Drawable {
    private final PVector start;
    private final PVector size;
    private String text;
    private Consumer<MouseClickEvent> listener;

    public Button(String text, PVector start, PVector size) {
        this.start = start.copy().add(new PVector(10, 10));
        this.size = size;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setListener(Consumer<MouseClickEvent> listener) {
        this.listener = listener;
    }

    @Override
    public void draw(DrawContext context) {
        // claim space
        context.claimRegion(size.copy().add(new PVector(start.x, start.y * 2)));

        final var applet = context.applet();

        context.applyStyle();
        applet.rect(start.x, start.y, size.x, size.y);

        context.applyTextStyle();
        applet.text(text, start.x + 0.1f * size.x, start.y + size.y - 0.3f * size.y);

        if (listener != null && context.mouseClicked() != null && context.region().relativeRegion(start, size).isInside(context.mouseClicked().position())) {
            listener.accept(context.mouseClicked()); ;
        }

    }
}
