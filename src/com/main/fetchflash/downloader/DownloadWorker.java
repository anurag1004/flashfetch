package com.main.fetchflash.downloader;

import com.main.fetchflash.constants.Constants;
import com.main.fetchflash.constants.EventType;
import com.main.fetchflash.model.event.ProgressEvent;
import com.main.fetchflash.progressdispatcher.ProgressEventDispatcher;
import com.main.fetchflash.model.task.Task;
import com.main.fetchflash.util.FileByeSizeRetriever;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
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
    private final ProgressEventDispatcher progressEventDispatcher;
    private Task task;
    private File partFile;
    private volatile boolean isPaused = false, isCancel = false;
    private static final int INPUT_BUFFER_SIZE = Constants.INPUT_BUFFER_SIZE;
    private static final int OUTPUT_BUFFER_SIZE = Constants.OUTPUT_BUFFER_SIZE;


    public WorkerAssistant(int start, int end, int partIndex, Task task, ProgressEventDispatcher progressEventDispatcher) throws IOException {
        this.start = start;
        this.end = end;
        this.partIndex = partIndex;
        this.progressEventDispatcher = progressEventDispatcher;
        this.task = task;
        this.partFile = createTempFile(task.getFileName(), partIndex);
    }

    @Override
    public void run() {
        try {
            progressEventDispatcher.emitProgress(new ProgressEvent(task, 0, EventType.DOWNLOADING, "Assistant started: "+partIndex));
            URL url = new URL(task.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Range", "bytes=" + start + "-" + (end - 1));
            double progress = 0;
            try (BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE)) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(partFile), OUTPUT_BUFFER_SIZE);
                buffer = new byte[INPUT_BUFFER_SIZE];
                int bytesRead;
                int totalBytesRead = 0;
                while ((bytesRead = bis.read(buffer)) != -1 && !isCancel) {
                    bos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    progress = ((double) totalBytesRead / (end - start)) * 100;
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, partIndex, (float) progress, EventType.DOWNLOADING, "Assistant: "+partIndex));
//                    progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, false, "Assistant: "+partIndex));
                    int pausedEmitCnt = 0;
                    while (isPaused) {
                        if(pausedEmitCnt==0) {
                            pausedEmitCnt = 1;
                            progressEventDispatcher.emitProgress(new ProgressEvent(task, partIndex, (float) progress, EventType.PAUSED, "download paused"));
//                            progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, false, "download paused"));
                        }
                        Thread.onSpinWait();
                    }
                }
                bos.flush(); bis.close(); bos.close();
                if(isCancel){
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, partIndex, (float) progress, EventType.CANCELED, "download canceled"));
//                    progressEventDispatcher.emitProgress(new ProgressEvent(task, (float) progress, true, "download canceled"));
                    buffer = new byte[]{}; // empty the buffer
                }else{
                    progressEventDispatcher.emitProgress(new ProgressEvent(task, partIndex,100, EventType.COMPLETED, "download completed"));
//                    progressEventDispatcher.emitProgress(new ProgressEvent(task, 100, true, "download completed"));
                }
            }
        } catch (Exception e) {
            ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Runtime exception: Assistant :(");
            exceptionEvent.setException(e);
            progressEventDispatcher.emitProgress(exceptionEvent);
            buffer = new byte[]{};
        }
    }
    private File createTempFile(String filename, int partIndex) throws IOException {
        return File.createTempFile(filename+"_part_" + partIndex + "_", ".tmp", new File(task.getOutputLocation()));
    }
    public File getPartFile() {
        return partFile;
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
    private static final int INPUT_BUFFER_SIZE = Constants.INPUT_BUFFER_SIZE;
    private static final int OUTPUT_BUFFER_SIZE = Constants.OUTPUT_BUFFER_SIZE;
    private static final int MAX_PARTS = 4;

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

            ExecutorService executor = Executors.newFixedThreadPool(MAX_PARTS);
            long startTime = System.currentTimeMillis();
            int partSize = (int)fileSize / MAX_PARTS;
            for (int i = 0; i < MAX_PARTS; i++) {
                int start = i * partSize;
                int end = (i == MAX_PARTS - 1) ? (int)fileSize : (i + 1) * partSize;
                progressEventDispatcher.emitProgress(new ProgressEvent(task, 0, EventType.DOWNLOADING, "Creating worker assistant: "+i));
                WorkerAssistant workerAssistant = new WorkerAssistant(start, end, i, task, progressEventDispatcher);
                assistants.add(workerAssistant);
                executor.execute(workerAssistant);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Failed to terminate executor");
                exceptionEvent.setException(e);
                progressEventDispatcher.emitProgress(exceptionEvent);
            }
            if(isCancel){
                progressEventDispatcher.emitProgress(new ProgressEvent(task, -1, EventType.CANCELED, "Worker: Download canceled for task:"+task.getTaskId()));
            }else {
                long elapsed = System.currentTimeMillis()-startTime;
                joinParts(task.getOutputLocation() + File.separator + fileName, assistants);
                progressEventDispatcher.emitProgress(new ProgressEvent(task, 100f, EventType.COMPLETED, String.format("Download complete :) \nTime taken: %.2f s\n",(float)elapsed/1000f)));
            }
        } catch (IOException e) {
            ProgressEvent exceptionEvent = new ProgressEvent(new Task("","",""), -1, EventType.ERROR, "Unexpected runtime exception: Worker :(");
            exceptionEvent.setException(e);
            progressEventDispatcher.emitProgress(exceptionEvent);
        }
    }
    private void joinParts(String outputFile, List<WorkerAssistant> assistants) throws IOException {
        progressEventDispatcher.emitProgress(new ProgressEvent(task, 100f, EventType.COMPLETED, String.format("Joining parts: "+task.getTaskId())));
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            BufferedOutputStream bos = new BufferedOutputStream(fos, OUTPUT_BUFFER_SIZE);
            for (WorkerAssistant assistant : assistants) {
                File partFile = assistant.getPartFile();
                try (FileInputStream fis = new FileInputStream(partFile)) {
                    BufferedInputStream bis = new BufferedInputStream(fis, INPUT_BUFFER_SIZE);
                    byte[] buffer = new byte[INPUT_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
                Files.delete(partFile.toPath());
            }
            bos.flush();
            bos.close();
        }
        progressEventDispatcher.emitProgress(new ProgressEvent(task, 100f, EventType.COMPLETED, String.format("Joining parts finished: "+task.getTaskId())));
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
