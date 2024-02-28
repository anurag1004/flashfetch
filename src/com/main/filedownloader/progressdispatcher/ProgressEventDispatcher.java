package com.main.filedownloader.progressdispatcher;

import com.main.filedownloader.model.event.ProgressEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProgressEventDispatcher {
    BlockingQueue<ProgressEvent> progressEvents;
    public ProgressEventDispatcher(BlockingQueue<ProgressEvent> progressEvents){
        this.progressEvents = progressEvents;
    }
    public void emitProgress(ProgressEvent progressEvent){
        new Thread(()->{
            try {
                this.progressEvents.put(progressEvent);
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
