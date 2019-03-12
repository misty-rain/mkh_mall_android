package com.mkh.mobilemall.support.file;

public class FileDownloaderHttpHelper {
    public static interface DownloadListener {
        public void pushProgress(int progress, int max);
    }
}
