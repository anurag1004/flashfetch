# FlashFetch

FlashFetch is a Java package designed for efficient and concurrent file downloading. It provides a simple and intuitive API for managing download tasks, allowing you to add, pause, resume, and cancel downloads with ease.

## Features

- **Concurrent Downloads**: FlashFetch supports concurrent downloading of multiple files, maximizing the utilization of available network resources.
- **Multi-part Downloading**: Files can be downloaded in multiple parts, improving download speed and reliability, especially for larger files.
- **Non-blocking Operations**: The download tasks run asynchronously, allowing you to continue working with your application while files are being downloaded in the background.
- **Download Queue Management**: Easily add, pause, resume, and cancel download tasks in the queue.
- **Intuitive API**: The package provides a straightforward and user-friendly API for managing downloads.

## Usage

### Getting Started

1. Obtain an instance of the `DownloadManager` by calling `FetchFlash.getDownloader()`:

```java
import com.example.flashfetch.FetchFlash;
import com.example.flashfetch.DownloadManager;

DownloadManager downloader = FetchFlash.getDownloader();
```
Add a new download task to the queue using the addToDownloadQueue method. This method takes three parameters: the URL of the file to download, the output location where the file should be saved, and the desired filename. It returns a Task object representing the download task.
java
```java
String url = "https://example.com/file.zip";
String outputLocation = "/path/to/downloads";
String filename = "file.zip";
Task task = downloader.addToDownloadQueue(url, outputLocation, filename);
```

### Controlling Download Tasks

You can control the state of a download task using the following methods:

**pauseDownload(int taskId)**: Pauses the download task with the specified task ID.</br>
**resumeDownload(int taskId)**: Resumes the paused download task with the specified task ID.</br>
**cancelDownload(int taskId)**: Cancels the download task with the specified task ID.</br>
```java
int taskId = task.getId();
downloader.pauseDownload(taskId);
downloader.resumeDownload(taskId);
downloader.cancelDownload(taskId);
```
### Exiting the Application
To gracefully exit the application and cancel all tasks in the download queue, call the exit method on the DownloadManager instance:
```java
downloader.exit();
```
### Handling Download Events

> **Note:** The event handling feature is not yet implemented in the current version of FlashFetch. It will be introduced in a future release.

FlashFetch will provide a way to handle events related to download tasks, such as progress updates, completion, and errors. You will be able to implement the `DownloadListener` interface and register your listener with the `DownloadManager`.

```java
import com.example.flashfetch.DownloadListener;
import com.example.flashfetch.DownloadEvent;

class MyDownloadListener implements DownloadListener {
   @Override
   public void onProgressUpdate(DownloadEvent event) {
       // Handle progress update
   }

   @Override
   public void onCompleted(DownloadEvent event) {
       // Handle download completion
   }

   @Override
   public void onError(DownloadEvent event) {
       // Handle download error
   }
}

// Register the listener with the DownloadManager (not yet implemented)
MyDownloadListener listener = new MyDownloadListener();
// downloader.addListener(listener);
