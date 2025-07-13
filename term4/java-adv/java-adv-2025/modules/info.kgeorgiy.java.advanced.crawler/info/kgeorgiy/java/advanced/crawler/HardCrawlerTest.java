package info.kgeorgiy.java.advanced.crawler;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.ToLongBiFunction;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HardCrawlerTest extends EasyCrawlerTest {
    public HardCrawlerTest() {
    }

    protected HardCrawlerTest(final ToLongBiFunction<CrawlerTask, String> test) {
        super(test);
    }

    @Test
    public void test20_singleConnectionPerHost() throws IOException {
        test("https://itmo.ru", 2, UNLIMITED, UNLIMITED, 1, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test21_limitedConnectionsPerHost() throws IOException {
        test("https://itmo.ru", 2, UNLIMITED, UNLIMITED, 2, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test22_limitedConnectionsPerformance() throws IOException {
        testPerformance(2, 3200);
        testPerformance(10, 900);
    }

    private void testPerformance(final int perHost, final double target) throws IOException {
        checkTime(target, test("http://www.kgeorgiy.info", 3, UNLIMITED, UNLIMITED, perHost, MAJOR_TIMEOUT * 2, MAJOR_TIMEOUT));
    }
}
