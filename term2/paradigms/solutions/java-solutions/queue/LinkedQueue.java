package queue;

import java.util.*;

// Model: a[head] ... a[tail]
// Inv: for i = head ... tail: a[i] != null
// Let: immutable(n): for i = tail ... head: a'[i] == a[i]
public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[tail'] = element &&
    //       immutable(n)
    @Override
    public void enqueueImpl(Object element) {
        Node newNode = new Node(element, null);
        if (isEmpty()) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    // Pre: n > 0
    // Post: R = a[head] && n' = n - 1 && immutable(n')
    @Override
    public Object dequeueImpl() {
        Object value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        return value;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // Pre: true
    // Post: R = a[head] && immutable(n)
    public Object element() {
        return head.value;
    }

    private static class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    // Pre: n = size && n > 1
    // Post: forall i=1..n:  a[i] != a[i - 1]
    @Override
    public void dedupImpl() {
        Node current = head;
        while (current != null && current.next != null) {
            if (current.value.equals(current.next.value)) {
                current.next = current.next.next;
                if (current.next == null) {
                    tail = current;
                }
                size--;
            } else {
                current = current.next;
            }
        }
    }
}



