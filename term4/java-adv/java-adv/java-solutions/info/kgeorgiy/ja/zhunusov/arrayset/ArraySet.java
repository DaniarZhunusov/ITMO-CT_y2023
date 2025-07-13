package info.kgeorgiy.ja.zhunusov.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {
    private final List<E> elements;
    private final Comparator<? super E> comparator;

    public ArraySet() {
        this.elements = Collections.emptyList();
        this.comparator = null;
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        TreeSet<E> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        this.elements = List.copyOf(treeSet);
        this.comparator = comparator;
    }

    private ArraySet(List<E> elements, Comparator<? super E> comparator) {
        this.elements = elements;
        this.comparator = comparator;
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSetImpl(fromElement, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return subSetImpl(null, toElement, true);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return subSetImpl(fromElement, null, false);
    }

    private SortedSet<E> subSetImpl(E from, E to, boolean head) {
        if (from != null && to != null && compare(from, to) > 0) {
            throw new IllegalArgumentException("fromKey must be <= toKey");
        }
        int fromIndex = from == null ? 0 : index(from);
        int toIndex = to == null ? elements.size() : index(to);
        return new ArraySet<>(elements.subList(fromIndex, toIndex), comparator);
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        return comparator == null ? ((Comparable<? super E>) a).compareTo(b) : comparator.compare(a, b);
    }

    private void Empty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E first() {
        Empty();
        return elements.get(0);
    }

    @Override
    public E last() {
        Empty();
        return elements.get(elements.size() - 1);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) o, comparator) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    private int index(E e) {
        int pos = Collections.binarySearch(elements, e, comparator);
        return pos < 0 ? -pos - 1 : pos;
    }
}
