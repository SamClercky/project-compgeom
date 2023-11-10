package be.ulbvub.compgeom.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class SlidingIterator<T> implements Iterator<ArrayList<T>> {
    private final int windowSize;
    /**
     * Buffer that remembers the first N items of the backing buffer, so they can be repeated at the end
     * to form a cyclic window
     */
    private final ArrayDeque<T> initialBuffer;
    private final ArrayDeque<T> buffer;
    private final Iterator<T> backingIterator;

    public SlidingIterator(int windowSize, Iterator<T> backingIterator) {
        this.windowSize = windowSize;
        this.backingIterator = backingIterator;
        this.buffer = new ArrayDeque<>();
        this.initialBuffer = new ArrayDeque<>();

        for (var i = 0; i < this.windowSize && this.backingIterator.hasNext(); i++) {
            final var next = this.backingIterator.next();
            this.buffer.add(next);
            if (i + 1 < this.windowSize) // last item, we do not add to the initial buffer
                this.initialBuffer.add(next);
        }
    }

    @Override
    public boolean hasNext() {
        return this.backingIterator.hasNext() || !this.initialBuffer.isEmpty();
    }

    @Override
    public ArrayList<T> next() {
        // fill buffer until window is full or iterator depleted
        while (this.buffer.size() < this.windowSize && (this.backingIterator.hasNext() || !this.initialBuffer.isEmpty())) {
            final var next = this.backingIterator.next();
            if (next != null) {
                // backing iterator is not yet depleted -> keep on adding
                this.buffer.add(next);
            } else {
                // backing iterator is depleted, fill now with initial buffer
                this.buffer.add(Objects.requireNonNull(initialBuffer.pollFirst()));
            }
        }

        if (this.buffer.size() == this.windowSize) {
            var result = new ArrayList<>(this.buffer);

            this.buffer.pollFirst();

            return result;
        } else {
            return null;
        }
    }
}
