<p align="center">
  <img src="https://res.cloudinary.com/depkjmecv/image/upload/v1710795558/flash-fetch-logo_nln2qh.jpg" alt="FlashFetch Logo" width="200">
</p>

# FlashFetch

FlashFetch is a Java package designed for efficient and concurrent file downloading. It provides a simple and intuitive API for managing download tasks, allowing you to add, pause, resume, and cancel downloads with ease. FlashFetch requires **JDK 18** or later.

## Features

- **Concurrent Downloads**: FlashFetch supports concurrent downloading of multiple files, maximizing the utilization of available network resources.
- **Multi-part Downloading**: Files can be downloaded in multiple parts, improving download speed and reliability, especially for larger files.
- **Non-blocking Operations**: The download tasks run asynchronously, allowing you to continue working with your application while files are being downloaded in the background.
- **Download Queue Management**: Easily add, pause, resume, and cancel download tasks in the queue.
- **Intuitive API**: The package provides a straightforward and user-friendly API for managing downloads.
  
## Architecture

<img src="docs/flashfetch-arch.svg" alt="Architecture diagram">

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
int taskId = task.getTaskId();
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

FlashFetch provides a way to handle events related to download tasks, such as progress updates, completion, errors, and exit. To use the event handling feature, you need to create a class that implements the `FFListener` interface and overrides the respective methods. Then, you can pass your listener to the constructor call.

#### Example

```java
import com.example.flashfetch.FFListener;
import com.example.flashfetch.ProgressEvent;

class MyDownloadListener implements FFListener {
    @Override
    public void onProgressUpdate(ProgressEvent event) {
    // Handle progress update
        System.out.println("Download progress: " + event.getProgress() + "%");
    }
    
    @Override
    public void onCompleted(ProgressEvent event) {
    // Handle download completion
        System.out.println("Download completed: " + event.getTask().getTaskId());
    }
    
    @Override
    public void onError(ProgressEvent event) {
    // Handle download error
        System.err.println("Download error: " + event.getException().getMessage());
    }
    
    @Override
    public void onExit(ProgressEvent event) {
    // Handle download exit
        System.out.println("Download process exited.");
    }
}

// Register the listener with the DownloadManager
MyDownloadListener listener = new MyDownloadListener();
DownloadManager downloader = new DownloadManager(listener);
```

The `FFListener` interface defines the following methods:

- `onProgressUpdate(ProgressEvent event)`: This method is called when the download progress is updated, allowing you to handle progress updates for a specific download task. In the example, it prints the current download progress.
- `onCompleted(ProgressEvent event)`: This method is called when a download task is completed successfully, enabling you to perform any necessary actions after the download finishes. In the example, it prints the name of the downloaded file.
- `onError(ProgressEvent event)`: This method is called when an error occurs during the download process, providing you with the opportunity to handle errors gracefully. In the example, it prints the error message.
- `onExit(ProgressEvent event)`: This method is called when the you shutdown download manager or is exited unexpectedly. In the example, it prints a message indicating the download process has exited.

#### ProgressEvent

The `ProgressEvent` class encapsulates the information related to a specific event that occurs during the download process. It contains the following fields:

```java
public class ProgressEvent {
   private EventType eventType;
   private final Task task;
   private final float progress;
   private int partIdx = -1; // not set or unavailable
   private String message;
   private Exception exception;
   // ... other methods
}
```
- `eventType`: An enum representing the type of event (e.g., progress update, completion, error).
- `task`: The Task object associated with the event.
- `progress`: The current download progress represented as a float value between 0 and 100.00.
- `partIdx`: The index of the download part for which the event occurred (if applicable).
- `message`: A message associated with the event (e.g., error message).
- `exception`: An exception object representing any error that occurred during the download process (if applicable).
