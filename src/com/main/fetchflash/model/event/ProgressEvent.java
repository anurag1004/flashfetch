package com.main.fetchflash.model.event;

import com.main.fetchflash.constants.EventType;
import com.main.fetchflash.model.task.Task;

public class ProgressEvent {
    private EventType eventType;
    private final Task task;
    private final float progress;
    private int partIdx = -1; // not set or unavailable
    private String message;
    private Exception exception;

    public ProgressEvent(Task task, float progress, EventType eventType, String message){
        this.task = task;
        this.progress = progress;
        this.eventType = eventType;
        this.message = message;
    }
    public ProgressEvent(Task task, int partIdx, float progress, EventType eventType, String message){
        this.task = task;
        this.partIdx = partIdx;
        this.progress = progress;
        this.eventType = eventType;
        this.message = message;
    }

    public int getPartIdx() {
        return partIdx;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Task getTask() {
        return task;
    }

    public float getProgress() {
        return progress;
    }

    public String getMessage(){
        return this.message;
    }
}
