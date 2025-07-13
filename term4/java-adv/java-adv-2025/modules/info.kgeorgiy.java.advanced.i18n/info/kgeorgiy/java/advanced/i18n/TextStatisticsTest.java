package info.kgeorgiy.java.advanced.i18n;

import info.kgeorgiy.java.advanced.base.BaseTest;
import net.java.quickcheck.collection.Pair;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Basic tests.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TextStatisticsTest extends BaseTest {
    public static final Locale RU = Locale.forLanguageTag("ru-RU");
    public static final Locale EN = Locale.forLanguageTag("en-US");

    public static final Path WORK_DIR = Path.of("__%s__".formatted(TextStatisticsTest.class.getSimpleName()));

    @AfterAll
    public static void afterAll() throws IOException {
        Files.walkFileTree(WORK_DIR, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    @Test
    public void test() {
        final List<Result> results = Stream.of("empty", "quine")
                .flatMap(file -> testInput(file, List.of(RU, EN)).stream())
                .toList();
        context.println("!!!  Passed: %d of %d".formatted(results.stream().filter(r -> r instanceof Success).count(), results.size()));
    }

    private List<Result> testInput(final String file, final List<Locale> inputLocales) {
        return inputLocales.stream()
                .flatMap(input -> Stream.of(RU, EN).map(output -> new Pair<>(input, output)))
                .map(pair -> testInputOutput(file, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    sealed interface Result {
        String name();
    }
    record Success(String name) implements Result {}
    record Failure(String name, Throwable error) implements Result {}


    private Result testInputOutput(final String file, final Locale inputLocale, final Locale outputLocale) {
        final String inputFilename = "%s.%s.in".formatted(file, inputLocale.toLanguageTag());
        final String outputFilename = "%s.%s.%s.out".formatted(file, inputLocale.toLanguageTag(), outputLocale.toLanguageTag());
        final Path inputFile = WORK_DIR.resolve(inputFilename);
        final Path outputFile = WORK_DIR.resolve(outputFilename);
        final String test = "%s/%s/%s".formatted(file, inputLocale.toLanguageTag(), outputLocale.toLanguageTag());

        context.println("Testing " + test);

        final long start = System.currentTimeMillis();
        try {
            if (!Files.exists(inputFile)) {
                try (final InputStream in = testFile(inputFilename)) {
                    Files.write(inputFile, in.readAllBytes());
                }
            }
            run(inputLocale.toLanguageTag(), outputLocale.toLanguageTag(), inputFile.toString(), outputFile.toString());
            verify(file, outputFile);
            context.println("Success for %s in %s ms".formatted(test, System.currentTimeMillis() - start));
            return new Success(test);
        } catch (final IOException | AssertionError e) {
            context.println("Error for %s in %s ms: (%s) %s".formatted(
                    test,
                    System.currentTimeMillis() - start,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            return new Failure(test, e);
        }
    }

    private static void verify(final String file, final Path outputFile) throws IOException {
        final List<String> actual = readFile(Files.newInputStream(outputFile));
        final List<String> expected = readFile(testFile(outputFile.getFileName().toString()));
        Assertions.assertEquals(expected.size(), actual.size(), "For %s: number of lines".formatted(file));
        IntStream.range(0, expected.size())
                .forEachOrdered(i -> Assertions.assertEquals(
                        expected.get(i),
                        actual.get(i),
                        "For %s: line %d".formatted(file, i + 1)
                ));
    }

    private static List<String> readFile(final InputStream in) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .map(line -> line.replaceAll("\\s+", " "))
                    .toList();
        }
    }

    private static InputStream testFile(final String file) {
        final InputStream stream = TextStatisticsTest.class.getResourceAsStream("tests/" + file);
        if (stream == null) {
            throw new AssertionError("Cannot find resource " + file);
        }
        return stream;
    }


    public static void run(final String... args) {
        final Method method;
        final Class<?> cut = BaseTest.loadClass();
        try {
            method = cut.getMethod("main", String[].class);
        } catch (final NoSuchMethodException e) {
            throw new AssertionError("Cannot find method main(String[]) of " + cut, e);
        }
        try {
            method.invoke(null, (Object) args);
        } catch (final IllegalAccessException e) {
            throw new AssertionError("Cannot call main(String[]) of " + cut, e);
        } catch (final InvocationTargetException e) {
            e.getCause().printStackTrace();
            throw new AssertionError("Error thrown: " + e.getCause().getMessage(), e.getCause());
        }
    }
}
