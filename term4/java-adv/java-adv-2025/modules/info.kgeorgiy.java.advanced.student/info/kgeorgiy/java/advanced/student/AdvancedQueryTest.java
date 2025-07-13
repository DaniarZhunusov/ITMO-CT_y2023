package info.kgeorgiy.java.advanced.student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedQueryTest extends GroupQueryTest implements AdvancedQuery {
    private final AdvancedQuery db = createCUT();

    public AdvancedQueryTest() {
    }

    @Test
    public void test31_testGetMostPopularName() {
        testGroups(this::getMostPopularName, db::getMostPopularName);
    }

    @Test
    public void test31_testGetLeastPopularName() {
        testGroups(this::getLeastPopularName, db::getLeastPopularName);
    }

    @Test
    public void test41_testGetFirstNames() {
        testGet(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test42_testGetLastNames() {
        testGet(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test43_testGetGroupNames() {
        testGet(this::getGroupNames, db::getGroupNames);
    }

    @Test
    public void test44_testGetFullNames() {
        testGet(this::getFullNames, db::getFullNames);
    }

    private <T> void testGet(
            final BiFunction<Collection<Group>, int[], List<T>> reference,
            final BiFunction<Collection<Group>, int[], List<T>> tested
    ) {
        final List<Group> groups = getGroupsByName(STUDENTS);
        final int max = groups.stream().mapToInt(group -> group.students().size()).max().orElse(0);
        for (int i = 0; i < STUDENTS.size(); i++) {
            final int[] args = RANDOM.ints(i, -1, max + 2).toArray();
            Assertions.assertEquals(reference.apply(groups, args), tested.apply(groups, args));
        }
    }

    public <R> void testGroups(final Function<Collection<Group>, R> reference, final Function<Collection<Group>, R> tested) {
        for (final List<Student> students : INPUTS) {
            final List<Group> groups = getGroupsByName(students);
            Assertions.assertEquals(reference.apply(groups), tested.apply(groups), "For " + students);
        }
    }

    // Reference implementation follows
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste

    @Override
    public String getMostPopularName(final Collection<Group> groups) {
        final NavigableMap<String, Integer> counts = new TreeMap<>();
        for (final Group group : groups) {
            for (final String name : Set.copyOf(getFirstNames(group.students()))) {
                counts.merge(name, 1, Integer::sum);
            }
        }

        if (counts.isEmpty()) {
            return "";
        }

        final int max = Collections.max(counts.values());
        for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == max) {
                return entry.getKey();
            }
        }
        return "";
    }

    @Override
    public String getLeastPopularName(final Collection<Group> groups) {
        final NavigableMap<String, Integer> counts = new TreeMap<>();
        for (final Group group : groups) {
            for (final String name : Set.copyOf(getFirstNames(group.students()))) {
                counts.merge(name, 1, Integer::sum);
            }
        }

        if (counts.isEmpty()) {
            return "";
        }

        final int min = Collections.min(counts.values());
        for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == min) {
                return entry.getKey();
            }
        }
        return "";
    }

    @Override
    public List<String> getFirstNames(final Collection<Group> groups, final int[] indices) {
        final List<String> result = new ArrayList<>();
        for (final Group group : groups) {
            for (final int index : indices) {
                if (0 <= index && index < group.students().size()) {
                    result.add(group.students().get(index).firstName());
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final Collection<Group> groups, final int[] indices) {
        final List<String> result = new ArrayList<>();
        for (final Group group : groups) {
            for (final int index : indices) {
                if (0 <= index && index < group.students().size()) {
                    result.add(group.students().get(index).lastName());
                }
            }
        }
        return result;
    }

    @Override
    public List<GroupName> getGroupNames(final Collection<Group> groups, final int[] indices) {
        final List<GroupName> result = new ArrayList<>();
        for (final Group group : groups) {
            for (final int index : indices) {
                if (0 <= index && index < group.students().size()) {
                    result.add(group.students().get(index).groupName());
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final Collection<Group> groups, final int[] indices) {
        final List<String> result = new ArrayList<>();
        for (final Group group : groups) {
            for (final int index : indices) {
                if (0 <= index && index < group.students().size()) {
                    result.add(getFullName(group.students().get(index)));
                }
            }
        }
        return result;
    }
}
