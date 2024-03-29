package com.main.fetchflash.progressfetcher;

import java.util.concurrent.BlockingQueue;

import com.main.fetchflash.constants.EventType;
import com.main.fetchflash.model.event.ProgressEvent;
import com.main.fetchflash.model.task.Task;

public class ProgressFetcher implements Runnable{
    BlockingQueue<ProgressEvent> progressEvents;
    FFListener ffListener;
    public ProgressFetcher(BlockingQueue<ProgressEvent> progressEvents){
        this.progressEvents = progressEvents;
        ffListener = null;
    }

    public void setFfListener(FFListener listener) {
        // sets listener
        this.ffListener = listener;
    }

    @Override
    public void run() {
        while(true){
            try {
                ProgressEvent progressEvent = progressEvents.take();
                if(progressEvent.getTask().getTaskId()==-1){
                    break;
                }
                if(ffListener!=null){
                    EventType eventType = progressEvent.getEventType();
                    switch (eventType){
                        case ERROR -> ffListener.onError(progressEvent);
                        case DOWNLOADING, CANCELED, PAUSED -> ffListener.onProgressUpdate(progressEvent);
                        case COMPLETED -> ffListener.onCompleted(progressEvent);
                        case EXIT -> ffListener.onExit(progressEvent);
                    }
                }
//                System.out.println(progressEvent.getTask().getTaskId()+"|"+progressEvent.getProgress()+"|"+progressEvent.isFinished()+"|"+progressEvent.getMessage());
            }catch (Exception e){
                ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Runtime exception: Progressfetcher");
                exceptionEvent.setException(e);
                ffListener.onError(exceptionEvent);
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Progress fetcher shutting down...");
    }
}
