package info.kgeorgiy.java.advanced.crawler;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .depends(CachingDownloader.class)
            .add("easy", EasyCrawlerTest.class)
            .add("hard", HardCrawlerTest.class)
            .add("new-easy", NewEasyCrawlerTest.class)
            .add("new-hard", NewHardCrawlerTest.class)
            .add("advanced", AdvancedCrawlerTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}
