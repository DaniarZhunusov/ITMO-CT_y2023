package info.kgeorgiy.ja.zhunusov.iterative;

public class WorkerThread extends Thread {
    private final TaskQueue taskQueue;
    private final ParallelMapperImpl mapper;

    public WorkerThread(TaskQueue taskQueue, ParallelMapperImpl mapper) {
        this.taskQueue = taskQueue;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Runnable task = taskQueue.getTask(mapper.isShutdown());
                if (task == null) break;
                task.run();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
