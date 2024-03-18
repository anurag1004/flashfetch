package com.main.fetchflash.model.event;

import com.main.fetchflash.model.task.Task;

public class ProgressEvent {
    private final Task task;
    private final float progress;
    private final boolean isFinished;
    private String message;

    public ProgressEvent(Task task, float progress, boolean isFinished) {
        this.task = task;
        this.progress = progress;
        this.isFinished = isFinished;
    }
    public ProgressEvent(Task task, float progress, boolean isFinished, String message) {
        this.task = task;
        this.progress = progress;
        this.isFinished = isFinished;
        this.message = message;
    }
    public Task getTask() {
        return task;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isFinished() {
        return isFinished;
    }
    public String getMessage(){
        return this.message;
    }
}
