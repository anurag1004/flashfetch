package com.main.filedownloader;


import com.main.filedownloader.downloader.DownloadManager;
import com.main.filedownloader.model.task.Task;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DownloadManager downloadManager = new DownloadManager();
        String url1 = "https://hips.hearstapps.com/hmg-prod/images/little-cute-maltipoo-puppy-royalty-free-image-1652926025.jpg";
        String url2 = "https://github.com/anurag1004/downtube-desktop-app/releases/download/downtube_v1.0.1_windows/ytd_V1.0.1.msi";
        String outputLocation = "/Users/anuragverma/IdeaProjects/flashfetch/downloads";
        Task t1 = downloadManager.addToDownloadQueue(url2, outputLocation);
        Task t2 = downloadManager.addToDownloadQueue(url1, outputLocation);
        Thread.sleep(5000);
        downloadManager.pauseDownload(t1.getId());
        Thread.sleep(5000);
        downloadManager.resumeDownload(t1.getId());
        Thread.sleep(5000);
        downloadManager.cancelDownload(t1.getId());
//        Task t2 = downloadManager.addToDownloadQueue(url2, outputLocation);
//        Thread.sleep(3000);
//        downloadManager.addToDownloadQueue(url2, outputLocation);
//        System.out.println("Download completed!");
    }
}
