package com.main.fetchflash.model.task;

public class Task {
    private static int id = 0;
    private int taskId;
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

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getFileName() {
        return fileName;
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
