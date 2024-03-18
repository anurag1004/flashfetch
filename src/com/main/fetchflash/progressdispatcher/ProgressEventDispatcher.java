package com.main.fetchflash.progressdispatcher;

import com.main.fetchflash.model.event.ProgressEvent;

import java.util.concurrent.BlockingQueue;

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
