package info.kgeorgiy.ja.zhunusov.crawler;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Phaser;

public class CrawlLevelContext {
    final Queue<String> currentLevel;
    final Queue<String> nextLevel;
    final int depthLeft;
    final List<String> excludes;
    final CrawlerContext context;
    final Set<String> downloaded;
    final Phaser phaser;

    public CrawlLevelContext(Queue<String> currentLevel, Queue<String> nextLevel, int depthLeft,
                             List<String> excludes, CrawlerContext context, Set<String> downloaded, Phaser phaser) {
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
        this.depthLeft = depthLeft;
        this.excludes = excludes;
        this.context = context;
        this.downloaded = downloaded;
        this.phaser = phaser;
    }
}
