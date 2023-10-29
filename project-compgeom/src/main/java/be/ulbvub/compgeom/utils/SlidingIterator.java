package be.ulbvub.compgeom.utils;

import java.util.ArrayDeque;
import java.util.Iterator;

public class SlidingIterator<T> implements Iterator<T[]> {
    private final int windowSize;
    private final ArrayDeque<T> buffer;
    private final Iterator<T> backingIterator;

    public SlidingIterator(int windowSize, Iterator<T> backingIterator) {
        this.windowSize = windowSize;
        this.backingIterator = backingIterator;
        this.buffer = new ArrayDeque<>();

        for (var i = 0; i < this.windowSize && this.backingIterator.hasNext(); i++) {
            this.buffer.add(this.backingIterator.next());
        }
    }

    @Override
    public boolean hasNext() {
        return this.buffer.size() == this.windowSize || this.backingIterator.hasNext();
    }

    @Override
    public T[] next() {
        // fill buffer until window is full or iterator depleted
        while (this.buffer.size() < this.windowSize && this.backingIterator.hasNext()) {
            this.buffer.add(this.backingIterator.next());
        }

        @SuppressWarnings("unchecked")
        var result = (T[]) this.buffer.toArray();

        this.buffer.pollFirst();

        return result;
    }
}
