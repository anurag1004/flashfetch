package com.main.filedownloader;


import com.main.filedownloader.downloader.DownloadManager;
import com.main.filedownloader.model.task.Task;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DownloadManager downloadManager = new DownloadManager();
        String url1 = "https://download-video.akamaized.net/v3-1/download/8bbe56c2-be33-4e71-af9f-a2db6cd3d48c/28ac3d3a-5fad803f/cGV4ZWxzLWNsw6ltZW50LXByb3VzdC0xOTI0NzY2MCAoMjE2MHApLm1wNA?__token__=st=1710445799~exp=1710529482~acl=%2Fv3-1%2Fdownload%2F8bbe56c2-be33-4e71-af9f-a2db6cd3d48c%2F28ac3d3a-5fad803f%2FcGV4ZWxzLWNsw6ltZW50LXByb3VzdC0xOTI0NzY2MCAoMjE2MHApLm1wNA%2A~hmac=d91b0de36bfd8e42512ecf06f6299200c5fdc2047918502dd00bab4d39159500&r=dXMtZWFzdDE%3D";
        String url2 = "https://github.com/anurag1004/downtube-desktop-app/releases/download/downtube_v1.0.1_windows/ytd_V1.0.1.msi";
        String outputLocation = "/Users/anuragverma/IdeaProjects/flashfetch/downloads";
        Task t1 = downloadManager.addToDownloadQueue(url1, outputLocation, "vid.mp4");
//        Task t2 = downloadManager.addToDownloadQueue(url1, outputLocation);
//        Thread.sleep(7000);
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
