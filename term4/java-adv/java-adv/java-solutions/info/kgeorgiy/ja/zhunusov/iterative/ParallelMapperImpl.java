package info.kgeorgiy.ja.zhunusov.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final List<WorkerThread> workers;
    private final TaskQueue taskQueue = new TaskQueue();
    private boolean isShutdown = false;

    public ParallelMapperImpl(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        workers = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            WorkerThread worker = new WorkerThread(taskQueue, this);
            worker.start();
            workers.add(worker);
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {
        if (isShutdown) {
            throw new IllegalStateException("Mapper has been shut down");
        }

        ResultContainer<R> container = new ResultContainer<>(items.size());

        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            taskQueue.addTask(() -> {
                R result = f.apply(items.get(index));
                container.setResult(index, result);
            });
        }

        container.await();
        return container.getResults();
    }

    @Override
    public void close() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }

        boolean interrupted = false;
        for (WorkerThread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return isShutdown;
    }
}
