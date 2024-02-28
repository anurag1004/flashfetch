package com.main.filedownloader.progressfetcher;

import java.util.concurrent.BlockingQueue;
import com.main.filedownloader.model.event.ProgressEvent;
public class ProgressFetcher implements Runnable{
    BlockingQueue<ProgressEvent> progressEvents;
    public ProgressFetcher(BlockingQueue<ProgressEvent> progressEvents){
        this.progressEvents = progressEvents;
    }

    @Override
    public void run() {
        while(true){
            try {
                ProgressEvent progressEvent = progressEvents.take();
                System.out.println(progressEvent.getTask().getId()+"|"+progressEvent.getProgress()+"|"+progressEvent.isFinished()+"|"+progressEvent.getMessage());
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
