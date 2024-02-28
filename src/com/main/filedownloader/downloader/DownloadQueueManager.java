package com.main.filedownloader.downloader;

import com.main.filedownloader.progressdispatcher.ProgressEventDispatcher;
import com.main.filedownloader.model.event.ProgressEvent;
import com.main.filedownloader.model.task.Task;
import com.main.filedownloader.progressfetcher.ProgressFetcher;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadQueueManager {
    private final BlockingQueue<Task> taskQueue;
    private final DownloadMasterWorker masterWorker;
    public DownloadQueueManager() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.masterWorker = new DownloadMasterWorker(this.taskQueue);
        this.startMasterWorker();
    }
    public synchronized void addTask(Task task){
        try {
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
    public void startMasterWorker(){
        Thread t = new Thread(masterWorker);
        t.start();
    }
}
