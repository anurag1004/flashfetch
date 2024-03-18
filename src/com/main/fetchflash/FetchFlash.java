package com.main.fetchflash;


import com.main.fetchflash.downloader.DownloadManager;

public class FetchFlash {
    public static DownloadManager getDownloader() {
        return new DownloadManager();
    }
}
