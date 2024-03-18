package com.main.fetchflash.downloader;

import com.main.fetchflash.model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadQueueManager {
    private final BlockingQueue<Task> taskQueue;
    private final DownloadMasterWorker masterWorker;
    private final List<Task> taskList;
    public DownloadQueueManager() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.taskList = new ArrayList<>();
        this.masterWorker = new DownloadMasterWorker(this.taskQueue);
        this.startMasterWorker();
    }
    public synchronized void addTask(Task task){
        try {
            taskList.add(task);
            taskQueue.put(task);
            System.out.println("Task added");
        }catch (Exception e){
            Thread.currentThread().interrupt();
        }
    }
    public void pauseTask(int taskId){
        masterWorker.pauseWorkerWithTaskId(taskId);
    }
    public void resumeTask(int taskId){
        masterWorker.resumeWorkerWithTaskId(taskId);
    }
    public void cancelTask(int taskId){
        masterWorker.cancelWorkerWithTaskId(taskId);
    }
    public void cancelAllTasks(){
        for(Task task: taskList){
            this.cancelTask(task.getTaskId());
        }
        Task exitTask = new Task("","","");
        exitTask.setTaskId(-1);
        this.addTask(exitTask);
    }
    public void startMasterWorker(){
        Thread t = new Thread(masterWorker);
        t.start();
    }
}
