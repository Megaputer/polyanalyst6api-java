package pa6api;

import java.io.File;

public abstract class ProgressHandler {
    public File file;
    public long totalFiles = 1;
    public long uploadedFiles = 0;
    public abstract void onProgress(float perc, long bytesUploaded, long bytesTotal);
}
