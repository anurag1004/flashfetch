package com.main.filedownloader.model.task;

public class Task {
    private static int id = 0;
    private final int taskId;
    private final String url;
    private final String outputLocation;
    private final String fileName;
    private float sizeInKbs;
    public Task(String url, String outputLoc, String fileName){
        taskId = ++Task.id;
        this.url = url;
        this.outputLocation = outputLoc;
        this.fileName = fileName;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return taskId;
    }

    public String getOutputLocation() {
        return outputLocation;
    }

    public String getUrl() {
        return url;
    }

    public float getSizeInKbs() {
        return sizeInKbs;
    }

    public void setSizeInKbs(float sizeInKbs) {
        this.sizeInKbs = sizeInKbs;
    }
}
