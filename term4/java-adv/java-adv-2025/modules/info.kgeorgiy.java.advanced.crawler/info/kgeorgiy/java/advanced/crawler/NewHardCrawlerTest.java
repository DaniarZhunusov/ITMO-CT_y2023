package info.kgeorgiy.java.advanced.crawler;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewHardCrawlerTest extends HardCrawlerTest {
    public NewHardCrawlerTest() {
        super(NewEasyCrawlerTest::testTask);
    }
}
