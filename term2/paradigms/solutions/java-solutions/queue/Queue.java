package queue;

// Model: a[head] ... a[tail]
// Inv: for i = head ... tail: a[i] != null
// Let: immutable(n): for i = tail ... head: a'[i] == a[i]
public interface Queue {
    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[tail'] = element &&
    //       immutable(tail')
    void enqueue(Object element);

    // Pre: true
    // Post: R = a[head] && immutable(head)
    Object dequeue();

    // Pre: true
    // Post: R = a[head] && immutable(head)
    Object element();

    // Pre: true
    // Post: R = (size == 0) && immutable(head, tail)
    boolean isEmpty();

    // Pre: true
    // Post: head = 0 && tail = 0 && size = 0
    void clear();

    // Pre: true
    // Post: R = tail - head && immutable(head, tail)
    int size();

    // Pre: n = size && n > 1
    // Post: forall i=1..n:  a[i] != a[i - 1]
    void dedup();
}