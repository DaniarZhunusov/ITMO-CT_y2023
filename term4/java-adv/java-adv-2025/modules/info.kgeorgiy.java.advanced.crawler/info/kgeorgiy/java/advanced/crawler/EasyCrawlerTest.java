package info.kgeorgiy.java.advanced.crawler;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.ToLongBiFunction;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class EasyCrawlerTest extends BaseTest {
    private static final String SLOW = ".*(erformance|deep|Both)\\(\\)";

    protected static final int UNLIMITED = 100;
    protected static final int MINOR_TIMEOUT = 10;
    protected static final int MAJOR_TIMEOUT = 50;
    protected static final int REAL_TIMEOUT = -3;

    private final ToLongBiFunction<CrawlerTask, String> test;

    public EasyCrawlerTest() {
        this((task, name) -> task.test(
                Crawler.class,
                crawler -> crawler.download(task.url(), task.depth()),
                url -> true
        ));
    }

    public EasyCrawlerTest(final ToLongBiFunction<CrawlerTask, String> test) {
        this.test = test;
    }

    @Test
    public void test01_singlePage() throws IOException {
        test("https://en.itmo.ru/en/page/50/Partnership.htm", 1);
        test("https://bars.itmo.ru", 1);
    }

    @Test
    public void test02_pageAndLinks() throws IOException {
        test("https://itmo.ru", 2);
        test("https://www.itmo.ru", 3);
    }

    @Test
    public void test03_invalid() throws IOException {
        test("https://itmo.ru/ru/educational-activity/voprosi_predlojeniya.htmvoprosy_i_predlozheniya.htmvoprosy_i_predlozheniya.htm", 1);
    }

    @Test
    public void test04_deep() throws IOException {
        for (int i = 1; i <= 5; i++) {
            test("http://www.kgeorgiy.info", i);
        }
    }

    @Test
    public void test05_noLimits() throws IOException {
        test(UNLIMITED, UNLIMITED, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test06_limitDownloads() throws IOException {
        test(10, UNLIMITED, MAJOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test07_limitExtractors() throws IOException {
        test(UNLIMITED, 10, MINOR_TIMEOUT, MAJOR_TIMEOUT);
    }

    @Test
    public void test08_limitBoth() throws IOException {
        test(10, 10, MAJOR_TIMEOUT, MAJOR_TIMEOUT);
    }

    @Test
    public void test09_performance() throws IOException {
        checkTime(3500, test(UNLIMITED, UNLIMITED, 500, 500));
    }

    @Test
    public void test10_realTimePerformance() throws IOException {
        checkTime(5000, test(UNLIMITED, UNLIMITED, REAL_TIMEOUT, REAL_TIMEOUT));
    }

    protected static void checkTime(final double target, final long time) {
        System.err.println("Time: " + time);
        Assertions.assertTrue(time > 0.8 * target, "Too parallel: " + time);
        Assertions.assertTrue(time < 1.2 * target, "Not parallel: " + time);
    }

    private void test(final String url, final int depth) throws IOException {
        test(url, depth, UNLIMITED, UNLIMITED, UNLIMITED, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    protected final long test(
            final int downloaders,
            final int extractors,
            final int downloadTimeout,
            final int extractTimeout
    ) throws IOException {
        final String url = "http://nerc.itmo.ru/subregions/index.html";
        return test(url, 3, downloaders, extractors, UNLIMITED, downloadTimeout, extractTimeout);
    }

    protected final long test(
            final String url,
            final int depth,
            final int downloaders,
            final int extractors,
            final int perHost,
            final int downloadTimeout,
            final int extractTimeout
    ) throws IOException {
        return test.applyAsLong(
                new CrawlerTask(url, depth, downloaders, extractors, perHost, downloadTimeout, extractTimeout),
                testName
        );
    }

    protected static <C extends Crawler> long testTask(
            final String type,
            final Class<C> crawlerType,
            final CrawlerMethod<C> method,
            final CrawlerTask task,
            final String testName,
            final List<List<String>> slow,
            final List<List<String>> fast,
            final BiPredicate<List<String>, String> filter
    ) {
        final List<List<String>> tests = testName.matches(SLOW) ? slow : fast;
        return tests.stream().mapToLong(test -> {
            try {
                return task.test(
                        crawlerType,
                        crawler -> method.download(crawler, task.url(), task.depth(), test),
                        url -> filter.test(test, url)
                );
            } catch (final AssertionError e) {
                throw new AssertionError(
                        "url = %s, depth = %s, %s = %s".formatted(task.url(), task.depth(), type, test),
                        e
                );
            }
        }).max().orElse(0);
    }

    protected interface CrawlerMethod<C extends Crawler> {
        Result download(C crawler, String url, int depth, List<String> test);
    }
}
