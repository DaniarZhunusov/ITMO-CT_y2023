package info.kgeorgiy.java.advanced.crawler;

import java.util.List;

/**
 * Crawls websites, filtering hosts by substrings.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface NewCrawler extends Crawler {
    /**
     * Downloads website up to specified depth.
     *
     * @param url start URL.
     * @param depth download depth.
     * @param excludes hosts containing one of given substrings are ignored.
     * @return download result.
     */
    Result download(String url, int depth, List<String> excludes);

    @Override
    default Result download(final String url, final int depth) {
        return download(url, depth, List.of());
    }
}
