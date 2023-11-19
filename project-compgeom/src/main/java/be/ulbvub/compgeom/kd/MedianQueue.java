package be.ulbvub.compgeom.kd;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class MedianQueue<T> extends AbstractQueue<T> {
    private final TreeSet<T> upperQueue;
    private final TreeSet<T> lowerQueue;

    public MedianQueue(Comparator<T> comparator) {
        upperQueue = new TreeSet<>(comparator);
        lowerQueue = new TreeSet<>(comparator.reversed());
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return upperQueue.size() + lowerQueue.size();
    }

    @Override
    public boolean offer(T t) {
        boolean result = upperQueue.add(t);
        while (lowerQueue.size() < upperQueue.size()) {
            result = result && lowerQueue.add(upperQueue.pollFirst());
        }

        return result;
    }

    @Override
    public T poll() {
        final var result = upperQueue.pollFirst();
        final var newMedian = lowerQueue.pollFirst();
        upperQueue.add(newMedian);

        return result;
    }

    @Override
    public T peek() {
        return upperQueue.first();
    }

    @Override
    public boolean remove(Object t) {
        if (upperQueue.remove(t)) {
            upperQueue.add(lowerQueue.pollFirst());
            return true;
        } else if (lowerQueue.remove(t)) {
            if (!upperQueue.isEmpty())
                lowerQueue.add(upperQueue.pollFirst());
            return true;
        } else {
            return false;
        }
    }
}
