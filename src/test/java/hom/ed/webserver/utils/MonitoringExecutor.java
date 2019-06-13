package hom.ed.webserver.utils;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringExecutor extends AbstractExecutorService {

    private final ExecutorService executor;

    private final AtomicInteger taskCounter = new AtomicInteger();

    public MonitoringExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        taskCounter.incrementAndGet();
        executor.execute(command);
    }

    public int getTaskCounter() {
        return taskCounter.get();
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }
}
