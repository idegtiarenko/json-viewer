package com.github.idegtiarenko.json.ui.components;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class BackgroundTaskExecutor {

    private final ExecutorService executor;
    private final LabeledProgressBar labeledProgressBar;

    public BackgroundTaskExecutor() {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            var thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        this.labeledProgressBar = new LabeledProgressBar();
        hideProgressBar();
    }

    public LabeledProgressBar getLabeledProgressBar() {
        return labeledProgressBar;
    }

    public <T> Future<?> submit(Task<T> task) {
        return executor.submit(() -> {
            try {
                var total = task.getTotalSize();
                if (total == 0) {
                    Platform.runLater(() -> task.onSuccess(null));
                    return;
                }
                Platform.runLater(this::showProgressBar);
                var result = task.execute(createProgressTracker(task.getName(), total));
                Platform.runLater(() -> task.onSuccess(result));
            } catch (Exception e) {
                Platform.runLater(() -> task.onFailure(e));
            } finally {
                Platform.runLater(this::hideProgressBar);
            }
        });
    }

    private IntConsumer createProgressTracker(String name, int total) {
        var interval = Math.max(1, total / 100);
        var last = new AtomicInteger();
        return current -> {
            if (current - last.get() >= interval) {
                last.set(current);
                Platform.runLater(() -> labeledProgressBar.update(new ProgressAndLabel(current, total, name)));
            }
        };
    }

    private void showProgressBar() {
        labeledProgressBar.setVisible(true);
    }

    private void hideProgressBar() {
        labeledProgressBar.update(new ProgressAndLabel(0, 1, null));
        labeledProgressBar.setVisible(false);
    }

    public interface Task<T> {
        String getName();

        int getTotalSize();

        T execute(IntConsumer onProgress);

        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
