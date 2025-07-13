package info.kgeorgiy.ja.zhunusov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements NewCrawler {
    private final Downloader downloader;
    private final int perHost;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    @Override
    public Result download(String url, int depth, List<String> excludes) {
        CrawlerContext context = new CrawlerContext();
        Set<String> downloaded = ConcurrentHashMap.newKeySet();

        Queue<String> current = new ConcurrentLinkedQueue<>();
        current.add(url);

        for (int d = 0; d < depth; d++) {
            Queue<String> next = new ConcurrentLinkedQueue<>();
            Phaser phaser = new Phaser(1);
            CrawlLevelContext ctx = new CrawlLevelContext(current, next, depth - d - 1, excludes, context, downloaded, phaser);
            processLevel(ctx);
            phaser.arriveAndAwaitAdvance();
            current = next;
        }

        return new Result(new ArrayList<>(downloaded), context.errors);
    }

    private void processLevel(CrawlLevelContext ctx) {
        for (String url : ctx.currentLevel) {
            if (isExcluded(url, ctx.excludes) || !ctx.context.visitedUrls.add(url)) {
                continue;
            }
            String host = getHostOrRecordError(url, ctx.context);
            if (host == null) {
                continue;
            }

            HostLimiter limiter = ctx.context.hostLimiterMap.computeIfAbsent(host, h -> new HostLimiter(perHost));
            ctx.phaser.register();
            downloaders.submit(() -> downloadTask(url, limiter, ctx));
        }
    }

    private void downloadTask(String url, HostLimiter limiter, CrawlLevelContext ctx) {
        try {
            Document doc = downloader.download(url);
            ctx.downloaded.add(url);
            if (ctx.depthLeft > 0) {
                ctx.phaser.register();
                extractors.submit(() -> extractLinks(url, doc, ctx));
            }
        } catch (IOException e) {
            ctx.context.errors.put(url, e);
        } finally {
            limiter.release();
            ctx.phaser.arrive();
        }
    }

    private void extractLinks(String url, Document doc, CrawlLevelContext ctx) {
        try {
            for (String link : doc.extractLinks()) {
                if (!isExcluded(link, ctx.excludes)) {
                    ctx.nextLevel.add(link);
                }
            }
        } catch (IOException e) {
            ctx.context.errors.put(url, e);
        } finally {
            ctx.phaser.arrive();
        }
    }

    private boolean isExcluded(String url, List<String> excludes) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        try {
            String host = URLUtils.getHost(url);
            return excludes.stream().anyMatch(host::contains);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String getHostOrRecordError(String url, CrawlerContext context) {
        try {
            return URLUtils.getHost(url);
        } catch (MalformedURLException e) {
            context.errors.put(url, e);
            return null;
        }
    }

    @Override
    public void close() {
        //note -- use close() methods
        downloaders.shutdownNow();
        extractors.shutdownNow();
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Usage: WebCrawler url [depth [downloads [extractors [perHost]]]]");
            return;
        }

        if (args.length < 1 || args.length > 5) {
            System.err.println("Command line arguments error.");
            return;
        }

        String url = args[0];
        int depth = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        int downloads = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        int extractors = args.length > 3 ? Integer.parseInt(args[3]) : 1;
        int perHost = args.length > 4 ? Integer.parseInt(args[4]) : 1;

        try (Crawler crawler = new WebCrawler(
                new CachingDownloader(100), downloads, extractors, perHost)) {
            crawler.download(url, depth);
        } catch (IOException e) {
            System.err.println("Error creating downloader: " + e.getMessage());
        }
    }
}
