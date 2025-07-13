package info.kgeorgiy.ja.zhunusov.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {

    private static final Comparator<Student> COMPARATORS = Comparator
            .comparing(Student::firstName)
            .thenComparing(Student::lastName)
            .thenComparing(Student::id);

    private <T> List<T> mapStudents(List<Student> students, Function<Student, T> mapper) {
        return students.stream().map(mapper).toList();
    }

    private List<Student> filterAndSort(Collection<Student> students, Predicate<Student> condition, Comparator<Student> comparator) {
        return students.stream().filter(condition).sorted(comparator).toList();
    }

    private List<Student> sortStudents(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator).toList();
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mapStudents(students, Student::firstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mapStudents(students, Student::lastName);
    }

    @Override
    public List<GroupName> getGroupNames(List<Student> students) {
        return mapStudents(students, Student::groupName);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mapStudents(students, st -> st.firstName() + " " + st.lastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Comparator.comparing(Student::id))
                .map(Student::firstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudents(students, Comparator.comparing(Student::id));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudents(students, COMPARATORS);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return filterAndSort(students, student -> student.firstName().equals(name), COMPARATORS);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return filterAndSort(students, student -> student.lastName().equals(name), Comparator.naturalOrder());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return filterAndSort(students, student -> student.groupName().equals(group), COMPARATORS);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream()
                .filter(student -> student.groupName().equals(group))
                .collect(Collectors.toMap(
                        Student::lastName,
                        Student::firstName,
                        BinaryOperator.minBy(Comparator.naturalOrder())
                ));
    }
}
