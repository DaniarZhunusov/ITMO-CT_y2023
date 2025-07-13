package info.kgeorgiy.java.advanced.student;

import java.util.List;
import java.util.Objects;

/**
 * Group of {@link Student students}.
 * @param name group name.
 * @param students students belong to this group.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Group(GroupName name, List<Student> students) {
    public Group(final GroupName name, final List<Student> students) {
        this.name = Objects.requireNonNull(name);
        this.students = List.copyOf(students);
    }

    @Override
    public String toString() {
        return "Group(%s, %s)".formatted(name, students);
    }
}
