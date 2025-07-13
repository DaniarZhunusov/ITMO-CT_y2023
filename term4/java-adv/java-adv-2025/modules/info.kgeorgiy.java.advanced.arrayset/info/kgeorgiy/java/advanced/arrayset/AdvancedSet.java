package info.kgeorgiy.java.advanced.arrayset;

import java.util.Map;
import java.util.NavigableSet;

/**
 * Interface for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-arrayset">ArraySet</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @param <E> element type.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedSet<E> extends NavigableSet<E> {
    @Override
    AdvancedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive);

    @Override
    AdvancedSet<E> headSet(E toElement, boolean inclusive);

    @Override
    AdvancedSet<E> tailSet(E fromElement, boolean inclusive);

    @Override
    AdvancedSet<E> subSet(E fromElement, E toElement);

    @Override
    AdvancedSet<E> headSet(E toElement);

    @Override
    AdvancedSet<E> tailSet(E fromElement);

    /**
     * Returns a new map that maps each item of this set to specified {@code value}.
     * <p>
     *     Expected performance: O(1).
     * </p>
     *
     * @param value value to map values for.
     * @return created map.
     * @param <V> value type.
     */
    <V> Map<E, V> asMap(final V value);
}
