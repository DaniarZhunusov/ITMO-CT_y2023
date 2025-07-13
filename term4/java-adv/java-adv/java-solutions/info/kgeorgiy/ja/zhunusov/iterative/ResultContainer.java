package info.kgeorgiy.ja.zhunusov.iterative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultContainer<R> {
    private final List<R> results;
    private int remainingTasks;

    public ResultContainer(int taskCount) {
        this.results = new ArrayList<>(Collections.nCopies(taskCount, null));
        this.remainingTasks = taskCount;
    }

    public void setResult(int index, R result) {
        synchronized (this) {
            results.set(index, result);
            remainingTasks--;
            if (remainingTasks == 0) {
                this.notifyAll();
            }
        }
    }

    public void await() throws InterruptedException {
        synchronized (this) {
            while (remainingTasks > 0) {
                this.wait();
            }
        }
    }

    public List<R> getResults() {
        synchronized (this) {
            return results;
        }
    }
}
