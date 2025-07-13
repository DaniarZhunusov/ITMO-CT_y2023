package queue;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Model: a[head] ... a[tail]
// Inv: for i = head ... tail: a[i] != null
// Let: immutable(n): for i = tail ... head: a'[i] == a[i]
public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[5];
    private int head;
    private int tail;

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[tail'] = element &&
    //       immutable(tail')

    @Override
    public void enqueueImpl(Object element) {
        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    // Pre: n > 0
    // Post: R = a[head] && n' = n - 1 && immutable(n')
    @Override
    public Object dequeueImpl() {
        Object value = elements[head];
        head = (head + 1) % elements.length;
        return value;
    }

    // Pre: true
    // Post: n' = n && immutable(n)
    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            int newCapacity = Math.max(2 * elements.length, capacity);
            Object[] newElements = new Object[newCapacity];
            if (head + size <= elements.length) {
                System.arraycopy(elements, head, newElements, 0, size);
            } else {
                System.arraycopy(elements, head, newElements, 0, elements.length - head);
                System.arraycopy(elements, 0, newElements, elements.length - head, tail);
            }
            elements = newElements;
            head = 0;
            tail = size;
        }
    }

    // Pre: true
    // Post: R = a[head] && immutable(n)
    public Object element() {
        return elements[head];
    }

    public void clear() {
        Arrays.fill(elements, null);
        head = 0;
        size = 0;
        tail = 0;
    }

    // Pre: predicate != null
    // Post: R = count
    public int countIf(Predicate<Object> predicate) {
        int count = 0;
        for (int i = head, countChecked = 0; countChecked < size; i = (i + 1) % elements.length, countChecked++) {
            if (predicate.test(elements[i])) {
                count++;
            }
        }
        return count;
    }

    // Pre: n = size && n > 1
    // Post: forall i=1..n:  a[i] != a[i - 1]
    @Override
    public void dedupImpl() {
        int newSize = 1;
        for (int i = 1; i < size; i++) {
            if (!elements[(head + i) % elements.length].equals(elements[(head + newSize - 1) % elements.length])) {
                elements[(head + newSize) % elements.length] = elements[(head + i) % elements.length];
                newSize++;
            }
        }

        while (size > newSize) {
            elements[tail] = null;
            tail = (tail - 1 + elements.length) % elements.length;
            size--;
        }
    }
}
