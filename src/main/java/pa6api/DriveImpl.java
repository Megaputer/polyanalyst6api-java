package pa6api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tus.java.client.TusURLMemoryStore;

class DriveImpl extends PA6APIImpl implements Drive {
    PA6TusClient tusClient;

    DriveImpl(String url, String sid) throws Exception {
        super(url, sid);
        tusClient = new PA6TusClient(sid);
        String uploadUrl = getAPIUrl("/file/upload");
        tusClient.setUploadCreationURL(new URL(uploadUrl));
        tusClient.enableResuming(new TusURLMemoryStore());
    }

    public void uploadFile(String source, String dest, ProgressHandler progress) throws Exception {
        final File file = new File(source);
        if (progress != null)
            progress.file = file;
        tusClient.uploadFile(dest, file, progress);
    }

    public void uploadFile(String source, String dest) throws Exception {
        final File file = new File(source);
        tusClient.uploadFile("", file, null);
    }

    public void uploadFolder(String source, String dest, ProgressHandler progress) throws Exception {
        File srcDir = new File(source);
        List<File> files = new ArrayList<>();
        for (File file : srcDir.listFiles())
            files.add(file);

        if (progress != null)
            progress.totalFiles = files.size();

        final ProgressHandler p = new ProgressHandler(){
            @Override
            public void onProgress(float perc, long bytesUploaded, long bytesTotal) {
                if (progress == null)
                    return;

                progress.file = file;
                if (perc == 100) {
                    progress.uploadedFiles++;
                    progress.onProgress(perc, bytesUploaded, bytesTotal);
                }
            }
        };
        for (File file : files) {
            p.file = file;
            tusClient.uploadFile(dest, file, p);
        }
    }

    public void createFolder(String name, String path) throws Exception {
        Map<String, String> post = new HashMap<>();
        post.put("name", name);
        post.put("path", path);

        sendSafe(requestAPI("/folder/create").POST(BodyPublishers.ofString(gson.toJson(post))).build());
    }

    public void deleteFolder(String name, String path) throws Exception {
        Map<String, String> post = new HashMap<>();
        post.put("name", name);
        post.put("path", path);

        sendSafe(requestAPI("/folder/delete").POST(BodyPublishers.ofString(gson.toJson(post))).build());
    }

    public void deleteFile(String name, String path) throws Exception {
        Map<String, String> post = new HashMap<>();
        post.put("name", name);
        post.put("path", path);

        sendSafe(requestAPI("/file/delete").POST(BodyPublishers.ofString(gson.toJson(post))).build());
    }

    public void downloadFile(String remoteName, String remotePath, String localPath) throws Exception {
        Map<String, String> post = new HashMap<>();
        post.put("name", remoteName);
        post.put("path", remotePath);
        HttpResponse<String> resp = sendSafe(requestAPI("/file/download").POST(BodyPublishers.ofString(gson.toJson(post))).build());
        Map<String, String> json = gson.fromJson(resp.body(), Map.class);

        InputStream is = sendAndGetStream(request("/download?uid=" + json.get("uid")).GET().build()).body();
        
        String outputPath = localPath;
        if (!outputPath.endsWith("/") && !outputPath.endsWith("\\"))
            outputPath += File.separator;
        outputPath += remoteName;

        OutputStream os = new FileOutputStream(outputPath);
        int readSize = -1;
        byte buf[] = new byte[1024 * 512];
        while ((readSize = is.read(buf)) != -1) {
            os.write(buf, 0, readSize);
        }
    }
}
