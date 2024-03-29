package com.main.fetchflash.progressfetcher;

import java.util.concurrent.BlockingQueue;
import com.main.fetchflash.model.event.ProgressEvent;
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
                if(progressEvent.getTask().getTaskId()==-1){
                    break;
                }
                //System.out.println(progressEvent.getTask().getId()+"|"+progressEvent.getProgress()+"|"+progressEvent.isFinished()+"|"+progressEvent.getMessage());
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Progress fetcher shutting down...");
    }
}
