package com.main.filedownloader;


import com.main.filedownloader.downloader.DownloadManager;
import com.main.filedownloader.model.task.Task;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DownloadManager downloadManager = new DownloadManager();
        String url1 = "https://d2b9iixc2wl3as.cloudfront.net/latest/AdobeLightroomDownloader.dmg";
        String url2 = "https://github.com/anurag1004/downtube-desktop-app/releases/download/downtube_v1.0.1_windows/ytd_V1.0.1.msi";
        String outputLocation = "/Users/anuragverma/IdeaProjects/flashfetch/downloads";
        Task t1 = downloadManager.addToDownloadQueue(url1, outputLocation, "lightroom.dmg");
        Task t2 = downloadManager.addToDownloadQueue(url2, outputLocation,"ytd1.msi");
        Thread.sleep(20000);
        Task t3 = downloadManager.addToDownloadQueue(url2, outputLocation,"ytd2.msi");
//        Thread.sleep(8000);
//        downloadManager.exit();
//        downloadManager.pauseDownload(t1.getId());
//        Thread.sleep(4000);
//        downloadManager.resumeDownload(t1.getId());
//        Thread.sleep(6000);
//        downloadManager.cancelDownload(t1.getId());
//        Task t2 = downloadManager.addToDownloadQueue(url1, outputLocation);
//        Thread.sleep(5000);
//        downloadManager.pauseDownload(t1.getId());
//        Thread.sleep(5000);
//        downloadManager.resumeDownload(t1.getId());
//        Thread.sleep(5000);
//        downloadManager.cancelDownload(t1.getId());
//        Task t2 = downloadManager.addToDownloadQueue(url2, outputLocation);
//        Thread.sleep(3000);
//        downloadManager.addToDownloadQueue(url2, outputLocation);
//        System.out.println("Download completed!");
    }
}
