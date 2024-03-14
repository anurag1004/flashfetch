package com.main.filedownloader.downloader;

import com.main.filedownloader.model.event.ProgressEvent;
import com.main.filedownloader.progressdispatcher.ProgressEventDispatcher;
import com.main.filedownloader.model.task.Task;
import com.main.filedownloader.util.FileByeSizeRetriever;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class WorkerAssistant implements Runnable{
    private final int start;
    private final int end;
    private final int partIndex;
    private byte[] buffer;
    private ProgressEventDispatcher progressEventDispatcher;
    private Task task;
    private volatile boolean isPaused = false, isCancel = false;


    public WorkerAssistant(int start, int end, int partIndex, Task task, ProgressEventDispatcher progressEventDispatcher) {
        this.start = start;
        this.end = end;
        this.partIndex = partIndex;
        this.progressEventDispatcher = progressEventDispatcher;
        this.task = task;
    }

    @Override
    public void run() {
        try {
            progressEventDispatcher.emitProgress(new ProgressEvent(task, 0, false, "Assistant started: "+partIndex));
            URL url = new URL(task.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Range", "bytes=" + start + "-" + (end - 1));
            double progress = 0;
            try (BufferedInputStream bis = new BufferedInputStream(conn.getInputStream())) {
                buffer = new byte[end - start];
                int offset = 0;
                int bytesRead;
                int totalBytesRead = 0;
                while (offset < buffer.length && (bytesRead = bis.read(buffer, offset, buffer.length - offset)) != -1 && !isCancel) {
                    offset += bytesRead;
                    totalBytesRead += bytesRead;
                    progress = ((double) totalBytesRead / (end - start)) * 100;
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, false, "Assistant: "+partIndex));
                    int pausedEmitCnt = 0;
                    while (isPaused) {
                        if(pausedEmitCnt==0) {
                            pausedEmitCnt = 1;
                            progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, false, "download paused"));
                        }
                        Thread.onSpinWait();
                    }
                }
                if(isCancel){
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, true, "download canceled"));
                    buffer = new byte[]{}; // empty the buffer
                }else{
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, 100, true, "download completed"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            buffer = new byte[]{};
        }
    }

    public void copyPartToFile(FileOutputStream fos) throws IOException {
        fos.write(buffer);
    }
    public void onPauseEvent(){
        this.isPaused = true;
    }
    public void onResumeEvent(){
        this.isPaused = false;
    }
    public void onCancelEvent(){
        this.isCancel = true;
    }
}
public class DownloadWorker implements Runnable{
    Task task;
    volatile boolean isPaused, isCancel = false;
    List<WorkerAssistant> assistants = new ArrayList<>();

    ProgressEventDispatcher progressEventDispatcher;
    public DownloadWorker(Task task, ProgressEventDispatcher progressEventDispatcher, boolean isPaused){
        this.task = task;
        this.progressEventDispatcher = progressEventDispatcher;
        this.isPaused = isPaused;
    }

    @Override
    public void run() {
        try {
            String fileURL = task.getUrl();
            float fileSize = FileByeSizeRetriever.getFileSize(fileURL);
            task.setSizeInKbs(fileSize);
            URL url = new URL(fileURL);
            String fileName = task.getFileName();
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.disconnect();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            int partSize = (int)fileSize / 4;
            for (int i = 0; i < 4; i++) {
                int start = i * partSize;
                int end = (i == 4 - 1) ? (int)fileSize : (i + 1) * partSize;
                progressEventDispatcher.emitProgress(new ProgressEvent(task, 0, false, "Creating worker assistant: "+i));
                WorkerAssistant workerAssistant = new WorkerAssistant(start, end, i, task, progressEventDispatcher);
                assistants.add(workerAssistant);
                executor.execute(workerAssistant);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isCancel){
                progressEventDispatcher.emitProgress(new ProgressEvent(task, -1, false, "Worker: Download canceled for task:"+task.getId()));
            }else {
                joinParts(task.getOutputLocation() + File.separator + fileName, assistants);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("Task started by worker: "+task.getUrl()+":"+Thread.currentThread().getName());
//        try {
//            String fileURL = task.getUrl();
//            float fileSize = FileByeSizeRetriever.getFileSize(fileURL);
//            task.setSizeInKbs(fileSize);
//            URL url = new URL(fileURL);
//            InputStream inputStream = url.openStream();
//            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
//            FileOutputStream outputStream = new FileOutputStream(task.getOutputLocation() + File.separator + fileName);
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            float totalBytesRead = 0f;
//            float progress = 0f;
//            while ((bytesRead = inputStream.read(buffer)) != -1 && !isCancel) {
//                int pausedEmitCnt = 0;
//                while (isPaused) {
//                    if(pausedEmitCnt==0) {
//                        pausedEmitCnt = 1;
//                        progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, false, "download paused"));
//                    }
//                    Thread.onSpinWait();
//                }
//                outputStream.write(buffer, 0, bytesRead);
//                totalBytesRead+=bytesRead;
//                progress = totalBytesRead / fileSize * 100; // Calculate progress
//                progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, false, "downloading"));
//            }
//            outputStream.close();
//            inputStream.close();
//            if(isCancel){
//                progressEventDispatcher.emitProgress(new ProgressEvent(task, progress, true, "download canceled"));
//            }else{
//                progressEventDispatcher.emitProgress(new ProgressEvent(task, 100, true, "download completed"));
//            }
//        }catch (Exception e){
//            Thread.currentThread().interrupt();
//        }
    }
    private void joinParts(String outputFile, List<WorkerAssistant> assistants) throws IOException {
        System.out.println("Joining parts: "+task.getId());
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (WorkerAssistant assistant : assistants) {
                assistant.copyPartToFile(fos);
            }
        }
        System.out.println("Joining parts finished: "+task.getId());
    }
    public void pause(){
        for(WorkerAssistant assistant: assistants){
            assistant.onPauseEvent();
        }
    }
    public void resume(){
        for(WorkerAssistant assistant: assistants){
            assistant.onResumeEvent();
        }
    }
    public void cancel(){
        this.isCancel = true;
        for(WorkerAssistant assistant: assistants){
            assistant.onCancelEvent();
        }
    }
}
