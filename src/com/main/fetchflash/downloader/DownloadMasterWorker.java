package com.main.fetchflash.downloader;

import com.main.fetchflash.model.event.ProgressEvent;
import com.main.fetchflash.progressdispatcher.ProgressEventDispatcher;
import com.main.fetchflash.model.task.Task;
import com.main.fetchflash.progressfetcher.ProgressFetcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadMasterWorker implements Runnable{
    private final BlockingQueue<Task> taskQueue;
    private final ProgressFetcher progressFetcher;
    private final BlockingQueue<ProgressEvent> progressEvents = new LinkedBlockingQueue<>();
    private final ProgressEventDispatcher progressEventDispatcher;
    private final Map<Integer, DownloadWorker> workersMap = new HashMap<>();
    public DownloadMasterWorker(BlockingQueue<Task> taskQueue){
        this.taskQueue = taskQueue;
        this.progressEventDispatcher = new ProgressEventDispatcher(progressEvents);
        this.progressFetcher = new ProgressFetcher(progressEvents);
        this.startProgressFetcher();
    }
    public void startProgressFetcher(){
        Thread t = new Thread(progressFetcher);
        t.start();
    }
    @Override
    public void run() {
        System.out.println("task queue manager started");
        Task exitTask = null;
        while(true){
            try {
                Task task = taskQueue.take();
                if(task.getTaskId()==-1){
                    // terminate
                    exitTask = task;
                    break;
                }
                System.out.println("Task taken out by master: " + task.getUrl());
                DownloadWorker downloadWorker = new DownloadWorker(task, progressEventDispatcher, false);
                Thread newWorkerThread = new Thread(downloadWorker);
                workersMap.put(task.getTaskId(), downloadWorker);
                newWorkerThread.start();
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Master shutting down...");
        for(int taskId: workersMap.keySet()){
            workersMap.get(taskId).cancel();
        }
        progressEventDispatcher.emitProgress(new ProgressEvent(exitTask, -1, false));
    }
    public void pauseWorkerWithTaskId(int taskId){
        if(workersMap.containsKey(taskId)) {
            workersMap.get(taskId).pause();
        }
    }
    public void resumeWorkerWithTaskId(int taskId){
        if(workersMap.containsKey(taskId)) {
            workersMap.get(taskId).resume();
        }
    }
    public void cancelWorkerWithTaskId(int taskId){
        if(workersMap.containsKey(taskId)) {
            workersMap.get(taskId).cancel();
        }
    }
}
