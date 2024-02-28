package com.main.filedownloader.downloader;

import com.main.filedownloader.model.event.ProgressEvent;
import com.main.filedownloader.progressdispatcher.ProgressEventDispatcher;
import com.main.filedownloader.model.task.Task;
import com.main.filedownloader.util.FileByeSizeRetriever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class DownloadWorker implements Runnable{
    Task task;
    volatile boolean isPaused, isCancel = false;
    ProgressEventDispatcher progressEventDispatcher;
    public DownloadWorker(Task task, ProgressEventDispatcher progressEventDispatcher, boolean isPaused){
        this.task = task;
        this.progressEventDispatcher = progressEventDispatcher;
        this.isPaused = isPaused;
    }
    @Override
    public void run() {
        System.out.println("Task started by worker: "+task.getUrl()+":"+Thread.currentThread().getName());
        try {
            String fileURL = task.getUrl();
            float fileSize = FileByeSizeRetriever.getFileSize(fileURL);
            task.setSizeInKbs(fileSize);
            URL url = new URL(fileURL);
            InputStream inputStream = url.openStream();
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            FileOutputStream outputStream = new FileOutputStream(task.getOutputLocation() + File.separator + fileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            float totalBytesRead = 0f;
            float progress = 0f;
            while ((bytesRead = inputStream.read(buffer)) != -1 && !isCancel) {
                int pausedEmitCnt = 0;
                while (isPaused) {
                    if(pausedEmitCnt==0) {
                        pausedEmitCnt = 1;
                        progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, false, "download paused"));
                    }
                    Thread.onSpinWait();
                }
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead+=bytesRead;
                progress = totalBytesRead / fileSize * 100; // Calculate progress
                progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, false, "downloading"));
            }
            outputStream.close();
            inputStream.close();
            if(isCancel){
                progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, true, "download canceled"));
            }else{
                progressEventDispatcher.emitProgress(new ProgressEvent(task, 100, true, "download completed"));
            }
        }catch (Exception e){
            Thread.currentThread().interrupt();
        }
    }
    public void pause(){
        this.isPaused = true;
    }
    public void resume(){
        this.isPaused = false;
    }
    public void cancel(){
        this.isCancel = true;
    }
}
