package info.kgeorgiy.ja.zhunusov.crawler;

import java.util.concurrent.Semaphore;

public class HostLimiter {
    private final Semaphore semaphore;

    public HostLimiter(int permits) {
        this.semaphore = new Semaphore(permits);
    }

    public void release() {
        semaphore.release();
    }
}
