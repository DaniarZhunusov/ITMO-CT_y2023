package info.kgeorgiy.java.advanced.student;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for easy version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StudentQueryTest extends BaseTest implements StudentQuery {
    protected static final Random RANDOM = new Random(2350238475230489753L);
    private static final List<String> FIRST_NAMES = Stream.of("Кирилл", "Александр", "Александр", "Александр", "Александр", "Кирилл", "Пётр", "Рауль", "Дана", "Валерий", "Георгий", "Александр", "Данил", "Алексей", "Кирилл", "Евгения", "Никита", "Алексей", "Алексей", "Александр", "Даниил", "Иван", "Дмитрий", "Иван", "Виталий", "Ростислав", "Нур", "Анастасия", "Владимир", "Амир", "Хомам", "Егор", "Александр", "Агата", "Николай", "Фёдор", "Никита", "Кирилл", "Иван", "Полина", "Алексей", "Егор", "Николай", "Андрей", "Кирилл", "Иван", "Валерия", "Дана", "Данил", "Ольга", "Иван", "Константин", "Яна", "Гордей", "Никита", "Кристина", "Даниил", "Олег", "Михаил", "Данияр", "Степан", "Артем", "Владислав", "Милана", "Вячеслав", "Арсений", "Ярослав", "Максим", "Леонид", "Егор", "Александр", "Вадим", "Захар", "Павел", "Николай", "Кирилл", "Алексей", "Вера", "Максим", "Даниил", "Артём", "Григорий", "Максим", "Елена", "Артём", "Дмитрий", "Ульяна", "Андрей", "Григорий", "Ярослав", "Татьяна", "Олег", "Иван", "Михаил", "Иван", "Иван", "Степан", "Илья", "Рафаэль", "Григорий", "Темуркан", "Никита", "Константин", "Никита", "Иван", "Александр", "Дарья", "Сергей", "Виктория", "Егор", "Айзат", "Эдуард", "Назар", "Ангелина", "Андрей", "Макар", "Виталия", "Артём", "Эмиль", "Матвей", "Алёна", "Анна", "Ольга", "Эмиль", "Булат", "Даниил", "Мария", "Матвей", "Максим", "Алексей", "Илья", "Роман", "Никон", "Данил", "Екатерина", "Данил", "Леонид", "Артём", "Мария", "Алан", "Даниил", "Глеб", "Даниил", "Максим", "Ришат", "Матвей", "Николь", "Эрнест", "Георгий", "Алексей", "Дмитрий", "Евгения", "Эльдар", "Ева", "Арсен", "Сергей", "Артемий", "Данила", "Тимофей", "Абулмуслим", "Никита", "Александр", "Макар", "Тимур", "Глеб", "Лиза", "Денис", "Николай", "Денис", "Владимир", "Екатерина", "Дмитрий", "Сергей", "Эдуард", "Константин", "Максим", "Илья", "Игорь", "Ольга", "Аркадий", "Анна", "Эя", "Виктор", "Александра", "Роман", "Фёдор", "Владимир", "Матвей", "Артём", "Игорь", "Ярослава", "Иван", "Захар", "Алексей", "Кирилл", "Артём", "Станислав", "Георгий", "Ярослав", "Александра", "Дмитрий", "Александр", "Андрей", "Семён", "Сергей", "Тимур", "Николай", "Алексей", "Роберт", "Искандар", "Максим", "Антон", "Павел", "Денис", "Рахман-Али", "Михаил", "Вероника", "Яна", "Егор", "Алина", "Лев", "Дмитрий", "Арсений", "Пётр", "Александр", "Софья", "Георгий", "Константин", "Ирина", "Владислав", "Камиля", "Кирилл", "Эдуард", "Григорий", "Иван", "Дмитрий", "Дарья", "Аяз", "Александр", "Никита", "Татьяна", "Денис", "Виктор", "Егор", "Данил", "Алексей", "Андрей", "Никита", "Евгений", "Максим", "Владимир", "Сергей", "Даниил", "Роман", "Дмитрий", "Данил", "Данила", "Татьяна", "Кирилл", "Федор", "Георгий", "Антон", "Владислав", "Михаил", "Даниил", "Никита", "Дмитрий", "Михаил", "Владислав", "Алексей", "Константин", "Николай", "Иван", "Дмитрий", "Даниил", "Ксения", "Иван", "Анна", "Кирилл", "Андрей", "Александр", "Роман", "Григорий", "Глеб", "Денис", "Юрий", "Егор", "Валерия", "Антон", "Тимофей", "Ярослав", "Екатерина", "Владимир", "Андрей", "Дмитрий", "Антон", "Альвиан", "Данил", "Егор", "Никита", "Алексей", "Михаил", "Анвар", "Анна", "Андрей", "Андрей", "Иван", "Максим", "Леонид", "Павел", "Максим", "Иван", "Егор", "Александр", "Сергей", "Адам", "Пётр", "Андрей", "Антон")
            .distinct().toList();
    private static final List<String> LAST_NAMES = Stream.of("Алфёров", "Бауэр", "Безушенко", "Бекетов", "Бражкин", "Бугрин", "Буйволов", "Вахитов", "Вешнякова", "Гилязов", "Голенков", "Гольдебаев", "Гончаренко", "Госунов", "Деменко", "Дин", "Диринг", "Ильин", "Калмыков", "Коваленко", "Ковинский", "Лаптев", "Лапшин", "Лебедев", "Луцкий", "Манзюк", "Марзук", "Новоселова", "Попов", "Сабиров", "Сакер", "Салов", "Сальников", "Сергеева", "Складнев", "Смирнов", "Соболев", "Струков", "Тарасов", "Трус", "Усков", "Фарафонов", "Шошинов", "Шустров", "Абакунов", "Акименко", "Аксенова", "Ашралиева", "Бадеев", "Барсукова", "Бобров", "Болотов", "Большакова", "Вахрушев", "Веретельников", "Глызина", "Доля", "Дроздов", "Ермольев", "Жунусов", "Захарьев", "Зызлаев", "Иванычев", "Игнатенкова", "Казаков", "Калиновский", "Каменский", "Карпов", "Ким", "Ковров", "Коновалов", "Кравцов", "Крюков", "Ласкин", "Литвинов", "Максимов", "Митрополов", "Мицкевич", "Мокин", "Парнюков", "Петров", "Попов", "Рыжевнин", "Сафронова", "Соболев", "Столбов", "Токарева", "Ульянов", "Червяков", "Черепня", "Шевякова", "Яковлев", "Александров", "Боин", "Бондаренко", "Борисов", "Борькин", "Бугрий", "Валиуллин", "Восканов", "Габеев", "Галимуллин", "Гордиенко", "Гордиенко", "Гусаров", "Дегтярев", "Дмитриева", "Дунаев", "Елизаветенкова", "Жукович", "Кашапов", "Кинзин", "Клюханов", "Круглова", "Крылов", "Кудяков", "Кузнецова", "Морозов", "Мухамадиев", "Немировский", "Никольская", "Пронкина", "Протченко", "Садыков", "Сафин", "Серов", "Скворцова", "Сологуб", "Солоницкий", "Степурин", "Тиунов", "Ноздреватых", "Парвицкий", "Редкин", "Рыжкова", "Савельев", "Сербин", "Смирнов", "Смирнова", "Сулейманов", "Сухоруков", "Трубин", "Урманов", "Федоров", "Хайруллин", "Шепелев", "Шукалович", "Ямалтдинов", "Фунин", "Цветков", "Чупров", "Шабас", "Шайхиев", "Шалимова", "Шумилов", "Шутов", "Юдинцев", "Ярмаркин", "Агафонов", "Атаев", "Баронов", "Беспалов", "Билык", "Гареев", "Головко", "Дадаева", "Дударев", "Зотов", "Зубарев", "Иванов", "Кириллова", "Коржиков", "Кубеш", "Кудрявцев", "Мараев", "Мартынов", "Матрухович", "Михаевич", "Михайлова", "Назаров", "Новикова", "Хартманн", "Чернышев", "Шовкопляс", "Юрченко", "Авдеев", "Альбов", "Бельский", "Бобков", "Булинин", "Галимова", "Горобец", "Зайцев", "Заречнев", "Иванцов", "Илюхин", "Калачев", "Карнаухов", "Кулезнев", "Маркова", "Меленцов", "Мещеряков", "Морозов", "Мочеков", "Муратов", "Муслухов", "Мыльников", "Петрасюк", "Пригара", "Сайфуллин", "Скорюкин", "Соколов", "Спицын", "Струментов", "Тимканов", "Тихонов", "Тычкова", "Федорова", "Федосеев", "Халили", "Цвей", "Швагурцев", "Абросимов", "Будаев", "Буйницкий", "Горулько", "Громов", "Добрынин", "Елизарова", "Захаров", "Исламова", "Кадомцев", "Кинзябулатов", "Кононов", "Кочелоров", "Кулебакин", "Левинца", "Магзянов", "Малков", "Маслаков", "Мацак", "Минаков", "Молчанов", "Панков", "Паршин", "Пластинин", "Подосенов", "Поскрёбышев", "Пухов", "Рудаков", "Рудер", "Салятов", "Смирнов", "Соловьев", "Шеин", "Широков", "Ярошенко", "Багина", "Бобовский", "Бондарев", "Бурунсузян", "Вотинов", "Галуза", "Грибакин", "Григорьев", "Завьялов", "Золотарев", "Зорин", "Казанцев", "Калугин", "Колосков", "Кононыхин", "Крамаренко", "Крюков", "Кулаковский", "Максимова", "Мандрика", "Мацкевич", "Мусинский", "Омельченко", "Попов", "Пруидзе", "Рокачевский", "Спиваковский", "Токарев", "Треногин", "Юлин", "Алешина", "Алиев", "Ананских", "Барнаков", "Бойцова", "Ветницкий", "Гебешт", "Зайцев", "Каско", "Колежук", "Колотов", "Конык", "Кудрявцев", "Кузьмичёв", "Кургузов", "Латыпов", "Маркина", "Мохов", "Пьянков", "Разинков", "Рысков", "Скарин", "Скобелин", "Суслов", "Трофимов", "Туисов", "Филатов", "Фокин", "Хромов", "Цыганов", "Чугунов", "Шварц")
            .distinct().toList();
    private static final List<GroupName> GROUPS = List.of(GroupName.values());

    protected static final List<Student> STUDENTS = RANDOM.ints(300)
            .mapToObj(id -> new Student(id, random(FIRST_NAMES), random(LAST_NAMES), random(GROUPS)))
            .flatMap(student -> Stream.of(
                    student,
                    new Student(RANDOM.nextInt(), student.firstName(), student.lastName(), student.groupName())
            ))
            .toList();

    protected static final List<List<Student>> INPUTS = IntStream.range(0, STUDENTS.size())
            .mapToObj(size -> {
                final List<Student> students = new ArrayList<>(STUDENTS).subList(0, size);
                Collections.shuffle(students, RANDOM);
                return List.copyOf(students);
            })
            .toList();

    private static <T> T random(final List<T> values) {
        return values.get(RANDOM.nextInt(values.size()));
    }

    private final StudentQuery db = createCUT();

    public StudentQueryTest() {
    }

    @Test
    public void test01_testGetFirstNames() {
        test(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test02_testGetLastNames() {
        test(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test03_testGetGroups() {
        test(this::getGroupNames, db::getGroupNames);
    }

    @Test
    public void test04_testGetFullNames() {
        test(this::getFullNames, db::getFullNames);
    }

    @Test
    public void test05_testGetDistinctFirstNames() {
        test(
                students -> List.copyOf(getDistinctFirstNames(students)),
                students -> List.copyOf(db.getDistinctFirstNames(students))
        );
    }

    @Test
    public void test06_testGetMaxStudentFirstName() {
        test(this::getMaxStudentFirstName, db::getMaxStudentFirstName);
    }

    @Test
    public void test07_testSortStudentsById() {
        test(this::sortStudentsById, db::sortStudentsById);
    }

    @Test
    public void test08_testSortStudentsByName() {
        test(this::sortStudentsByName, db::sortStudentsByName);
    }

    @Test
    public void test09_testFindStudentsByFirstName() {
        testBi(this::findStudentsByFirstName, db::findStudentsByFirstName, FIRST_NAMES);
    }

    @Test
    public void test10_testFindStudentsByLastName() {
        testBi(this::findStudentsByLastName, db::findStudentsByLastName, FIRST_NAMES);
    }

    @Test
    public void test11_testFindStudentsByGroup() {
        testBi(this::findStudentsByGroup, db::findStudentsByGroup, GROUPS);
    }

    @Test
    public void test12_findStudentNamesByGroup() {
        testBi(this::findStudentNamesByGroupList, db::findStudentNamesByGroupList, GROUPS);
    }

    public static <R> void test(final Function<List<Student>, R> reference, final Function<List<Student>, R> tested) {
        for (final List<Student> input : INPUTS) {
            Assertions.assertEquals(reference.apply(input), tested.apply(input), "For " + input);
        }
    }

    protected <T, U> void testBi(
            final BiFunction<List<Student>, U, T> reference,
            final BiFunction<List<Student>, U, T> tested,
            final List<U> values
    ) {
        final List<U> probes = new ArrayList<>(values);
        Collections.shuffle(probes, RANDOM);
        for (final U value : probes.subList(0, Math.min(values.size(), 10))) {
            context.context("Testing " + value, () ->
                    test(input -> reference.apply(input, value), input -> tested.apply(input, value)));
        }
    }

    // Reference implementation follows.
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste.

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.firstName());
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.lastName());
        }
        return result;
    }

    @Override
    public List<GroupName> getGroupNames(final List<Student> students) {
        final List<GroupName> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.groupName());
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(getFullName(student));
        }
        return result;
    }

    protected static String getFullName(final Student student) {
        return student.firstName() + " " + student.lastName();
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        int maxId = Integer.MIN_VALUE;
        String maxName = "";
        for (final Student student : students) {
            if (maxId < student.id()) {
                maxId = student.id();
                maxName = student.firstName();
            }
        }
        return maxName;
    }

    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted);
        return sorted;
    }

    private static final Comparator<Student> STUDENT_COMPARATOR = (a, b) -> {
        final int first = a.firstName().compareTo(b.firstName());
        if (first != 0) {
            return first;
        }
        final int last = a.lastName().compareTo(b.lastName());
        if (last != 0) {
            return last;
        }
        return Integer.compare(a.id(), b.id());
    };

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        sorted.sort(STUDENT_COMPARATOR);
        return sorted;
    }

    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String firstName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.firstName().equals(firstName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String lastName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.lastName().equals(lastName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.groupName().equals(group));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        final Map<String, String> result = new HashMap<>();
        for (final Student student : findStudentsByGroup(students, group)) {
            result.merge(student.lastName(), student.firstName(), BinaryOperator.minBy(Comparable::compareTo));
        }
        return result;
    }
}
