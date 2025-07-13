package info.kgeorgiy.ja.zhunusov.crawler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

public class CrawlerContext {
    final ConcurrentMap<String, HostLimiter> hostLimiterMap = new ConcurrentHashMap<>();
    final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    final ConcurrentMap<String, IOException> errors = new ConcurrentHashMap<>();
}
