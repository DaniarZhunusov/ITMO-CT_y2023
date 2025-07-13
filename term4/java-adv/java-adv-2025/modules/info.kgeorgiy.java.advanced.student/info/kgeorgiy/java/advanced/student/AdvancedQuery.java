package info.kgeorgiy.java.advanced.student;

import java.util.Collection;
import java.util.List;

/**
 * Advanced-version interface
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedQuery extends GroupQuery {
    /**
     * Returns the first name of the student such that the most number of groups has student with that name.
     * If there is more than one such name, the least one is returned.
     */
    String getMostPopularName(Collection<Group> groups);

    /**
     * Returns the first name of the student such that the least number of groups has student with that name.
     * If there is more than one such name, the least one is returned.
     */
    String getLeastPopularName(Collection<Group> groups);

    /**
     * Returns student {@link Student#firstName() first names} by indices for each group.
     * Skips index if there are no student with specified index in a group.
     * <p>
     * {@link #getFirstNames(List)} should be used to bulk-extract student names.
     */
    List<String> getFirstNames(Collection<Group> groups, final int[] indices);

    /**
     * Returns student {@link Student#lastName() last names} by indices for each group.
     * Skips index if there are no student with specified index in a group.
     * <p>
     * {@link #getLastNames(List)} should be used to bulk-extract student names.
     */
    List<String> getLastNames(Collection<Group> groups, final int[] indices);

    /**
     * Returns student {@link Student#groupName() group names} by indices for each group.
     * Skips index if there are no student with specified index in a group.
     * <p>
     * {@link #getGroupNames(List)} should be used to bulk-extract groups.
     */
    List<GroupName> getGroupNames(Collection<Group> groups, final int[] indices);

    /**
     * Returns full student name by indices for each group.
     * Skips index if there are no student with specified index in a group.
     * <p>
     * {@link #getFirstNames(List)} should be used to bulk-extract student names.
     */
    List<String> getFullNames(Collection<Group> groups, final int[] indices);
}
