package queue;

import java.util.*;
import java.util.function.Predicate;

// Model: a[head] ... a[tail]
// Inv: for i = head ... tail: a[i] != null
// Let: immutable(n): for i = tail ... head: a'[i] == a[i]
public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int size;
    private int head;
    private int tail;

    // Pre: element != null && queue != null
    // Post: n' = n + 1 &&
    //       a'[tail'] = element &&
    //       immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(queue, queue.size + 1);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
        queue.size++;
    }

    // Pre: n > 0 && queue != null
    // Post: R = a[head] && n' = n - 1 && immutable(n')
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = queue.elements[queue.head];
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;
        return value;
    }

    // Pre: queue != null
    // Post: n' = n && immutable(n)
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            int newCapacity = Math.max(2 * queue.elements.length, capacity);
            Object[] newElements = new Object[newCapacity];
            if (queue.head + queue.size <= queue.elements.length) {
                System.arraycopy(queue.elements, queue.head, newElements, 0, queue.size);
            } else {
                System.arraycopy(queue.elements, queue.head, newElements, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.head, queue.tail);
            }
            queue.elements = newElements;
            queue.head = 0;
            queue.tail = queue.size;
        }
    }


    // Pre: queue != null
    // Post: R = a[head] && immutable(n)
    public static Object element(ArrayQueueADT queue) {
        if (queue.size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return queue.elements[queue.head];
    }

    // Pre: queue != null
    // Post: R = (n == 0) && immutable(n)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    // Pre: queue != null
    // Post: n' = 0
    public static void clear(ArrayQueueADT queue) {
        Arrays.fill(queue.elements, null);
        queue.size = 0;
        queue.head = 0;
        queue.tail = 0;
    }

    // Pre: queue != null
    // Post: R = n && immutable(n)
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pre: predicate != null && queue != null
    // Post: R = count
    public static int countIf(ArrayQueueADT queue, Predicate<Object> predicate) {
        int count = 0;
        for (int i = queue.head, countChecked = 0; countChecked < queue.size; i = (i + 1) % queue.elements.length, countChecked++) {
            if (predicate.test(queue.elements[i])) {
                count++;
            }
        }
        return count;
    }
}