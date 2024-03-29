package com.main.fetchflash.downloader;

import com.main.fetchflash.constants.EventType;
import com.main.fetchflash.model.event.ProgressEvent;
import com.main.fetchflash.model.task.Task;
import com.main.fetchflash.progressfetcher.FFListener;

public class DownloadManager {
    private final DownloadQueueManager downloadQueueManager;
    private FFListener ffListener = null;
    public DownloadManager() {
        downloadQueueManager = new DownloadQueueManager();
        downloadQueueManager.startMasterWorker();
    }
    public DownloadManager(FFListener ffListener){
        downloadQueueManager = new DownloadQueueManager();
        this.ffListener = ffListener;
        downloadQueueManager.setFfListener(ffListener);
        downloadQueueManager.startMasterWorker();
    }
    public Task addToDownloadQueue(String url, String outputLocation, String filename){
        Task task = new Task(url, outputLocation, filename);
        try {
            downloadQueueManager.addTask(task);
        }catch (Exception e){
            ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Failed to add to download queue");
            exceptionEvent.setException(e);
            ffListener.onError(exceptionEvent);
        }
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
        try {
            downloadQueueManager.cancelAllTasks();
        }catch (Exception e){
            ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Failed to exit gracefully. Force shutting down");
            exceptionEvent.setException(e);
            ffListener.onError(exceptionEvent);
            System.exit(-1);
        }
    }
}
