package com.main.filedownloader.downloader;

import com.main.filedownloader.model.task.Task;
import com.main.filedownloader.progressfetcher.ProgressFetcher;

public class DownloadManager {
    private final DownloadQueueManager downloadQueueManager;
    public DownloadManager() {
        downloadQueueManager = new DownloadQueueManager();
    }
    public Task addToDownloadQueue(String url, String outputLocation, String filename){
        Task task = new Task(url, outputLocation, filename);
        downloadQueueManager.addTask(task);
        return task;
    }
    public void pauseDownload(int taskId){
        downloadQueueManager.pauseTask(taskId);
    }
    public void resumeDownload(int taskId){
        downloadQueueManager.resumeTask(taskId);
    }
    public void cancelDownload(int taskId){
        downloadQueueManager.cancelTask(taskId);
    }
    public void exit(){
        downloadQueueManager.cancelAllTasks();
    }
}
