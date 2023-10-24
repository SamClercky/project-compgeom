package be.ulbvub.compgeom;

import processing.core.PVector;

import java.util.function.Function;

public class Button implements Drawable {
    private final PVector start;
    private final PVector size;
    private String text;
    private Function<MouseClickEvent, Void> listener;

    public Button(String text, PVector start, PVector size) {
        this.start = start;
        this.size = size;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setListener(Function<MouseClickEvent, Void> listener) {
        this.listener = listener;
    }

    @Override
    public void draw(DrawContext context) {
        final var applet = context.applet();

        context.applyStyle();
        applet.rect(start.x, start.y, size.x, size.y);

        applet.text(text, start.x, start.y);

        if (listener != null && context.mouseClicked() != null && context.region().relativeRegion(start, size).isInside(context.mouseClicked().position())) {
            listener.apply(context.mouseClicked());
        }
    }
}
