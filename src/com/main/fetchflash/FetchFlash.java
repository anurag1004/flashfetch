package com.main.fetchflash;


import com.main.fetchflash.downloader.DownloadManager;
import com.main.fetchflash.progressfetcher.FFListener;

public class FetchFlash {
    public static DownloadManager getDownloader() {
        return new DownloadManager();
    }
    public static DownloadManager getDownloader(FFListener ffListener){
        return new DownloadManager(ffListener);
    }
}
