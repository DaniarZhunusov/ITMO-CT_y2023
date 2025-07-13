package queue;

import java.util.*;
import java.util.function.Predicate;

// Model: a[head] ... a[tail]
// Inv: for i = head ... tail: a[i] != null
// Let: immutable(n): for i = tail ... head: a'[i] == a[i]
public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int size;
    private static int head;
    private static int tail;

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[tail'] = element &&
    //       immutable(n)
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }

    // Pre: n > 0
    // Post: R = a[head] && n' = n - 1 && immutable(n')
    public static Object dequeue() {
        assert size > 0;
        Object value = elements[head];
        head = (head + 1) % elements.length;
        size--;
        return value;
    }

    // Pre: true
    // Post: n' = n && immutable(n)
    private static void ensureCapacity(int capacity) {
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
    public static Object element() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return elements[head];
    }

    // Pre: true
    // Post: R = (n == 0) && immutable(n)
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: n' = 0
    public static void clear() {
        Arrays.fill(elements, null);
        size = 0;
        head = 0;
        tail = 0;
    }

    // Pre: true
    // Post: R = n && immutable(n)
    public static int size() {
        return size;
    }

    // Pre: predicate != null
    // Post: R = count
    public static int countIf(Predicate<Object> predicate) {
        int count = 0;
        for (int i = head, countChecked = 0; countChecked < size; i = (i + 1) % elements.length, countChecked++) {
            if (predicate.test(elements[i])) {
                count++;
            }
        }
        return count;
    }
}
