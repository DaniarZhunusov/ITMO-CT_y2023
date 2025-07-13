package queue;

import java.util.*;

public abstract class AbstractQueue implements Queue {
    protected int size;

    @Override
    public void enqueue(Object element) {
        Objects.requireNonNull(element);
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object dequeue() {
        assert size > 0;
        Object dequeuedElement = dequeueImpl();
        size--;
        return dequeuedElement;
    }

    protected abstract Object dequeueImpl();


    @Override
    public abstract Object element();

    @Override
    public void dedup() {
        if (size <= 1) {
            return;
        }
        dedupImpl();
    }

    protected abstract void dedupImpl();


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public abstract void clear();
}