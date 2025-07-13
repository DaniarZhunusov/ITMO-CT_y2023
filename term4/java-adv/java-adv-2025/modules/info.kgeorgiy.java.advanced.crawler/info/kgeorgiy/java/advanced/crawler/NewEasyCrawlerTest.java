package info.kgeorgiy.java.advanced.crawler;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewEasyCrawlerTest extends EasyCrawlerTest {
    private static final List<List<String>> EXCLUDES = List.of(
            List.of(".ru"),
            List.of(".info"),
            List.of(".ru", ".info", ".com"),
            List.of(),
            List.of("a", "q", "v")
    );

    public NewEasyCrawlerTest() {
        super(NewEasyCrawlerTest::testTask);
    }

    protected static long testTask(final CrawlerTask task, final String testName) {
        return testTask(
                "excludes",
                NewCrawler.class,
                NewCrawler::download,
                task,
                testName,
                List.of(List.of()),
                EXCLUDES,
                (excludes, url) -> {
                    try {
                        return excludes.stream().noneMatch(URLUtils.getHost(url)::contains);
                    } catch (final MalformedURLException e) {
                        return false;
                    }
                }
        );
    }
}
