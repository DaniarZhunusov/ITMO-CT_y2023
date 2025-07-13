package info.kgeorgiy.ja.zhunusov.iterative;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {
    private final Queue<Runnable> queue = new LinkedList<>();

    public synchronized void addTask(Runnable task) {
        queue.add(task);
        notify();
    }

    public synchronized Runnable getTask(boolean isStopped) throws InterruptedException {
        while (queue.isEmpty() && !isStopped) {
            this.wait();
        }
        return isStopped ? null : queue.poll();
    }
}
