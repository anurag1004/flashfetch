package com.main.fetchflash.progressfetcher;

import com.main.fetchflash.model.event.ProgressEvent;

public interface FFListener {
    public void onProgressUpdate(ProgressEvent event);
    public void onCompleted(ProgressEvent event);
    public void onError(ProgressEvent event);
    public void onExit(ProgressEvent event);
}
