package info.kgeorgiy.java.advanced.student;

import java.util.Objects;

/**
 * Student information.
 * @param id unique student identifier.
 * @param firstName student first name.
 * @param lastName student last name.
 * @param groupName name of the group, this student belongs to.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Student(int id, String firstName, String lastName, GroupName groupName) implements Comparable<Student> {
    public Student {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(groupName);
    }

    /**
     * Compares students by {@link #id}.
     */
    @Override
    public int compareTo(final Student that) {
        return Integer.compare(id, that.id);
    }
}
