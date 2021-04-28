package pa6api;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;

import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusExecutor;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

public class PA6TusClient extends TusClient {
    protected int chunkSize = 1024 * 1024;
    protected String sid = "";

    PA6TusClient(String sid) {
        super();

        this.sid = sid;
        Map<String, String> hdr = new HashMap<>();
        hdr.put("cookie", "sid=" + sid);
        setHeaders(hdr);
    }

    @Override
    public void prepareConnection(HttpURLConnection connection) {
        super.prepareConnection(connection);
        
        if(connection instanceof HttpsURLConnection) {
            HttpsURLConnection secureConnection = (HttpsURLConnection) connection;

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                secureConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                secureConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                      return true;
                    }
                  });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };

    public void uploadFile(String folder, File file, ProgressHandler progress) throws ProtocolException, IOException {
        final TusUpload upload = new TusUpload(file);
        Map<String, String> metadata = new HashMap<>(upload.getMetadata());
        metadata.put("foldername", folder);
        upload.setMetadata(metadata);

        TusExecutor executor = new TusExecutor() {
            @Override
            protected void makeAttempt() throws ProtocolException, IOException {
                TusUploader uploader = resumeOrCreateUpload(upload);
                uploader.setChunkSize(chunkSize);

                do {
                    long totalBytes = upload.getSize();
                    long bytesUploaded = uploader.getOffset();
                    double p = (double)bytesUploaded / totalBytes * 100;
                    p = Math.floor(p * 100) / 100;
                    if (progress != null)
                        progress.onProgress((float)p, bytesUploaded, totalBytes);
                } while (uploader.uploadChunk() > -1);
                uploader.finish();
            }
        };
        executor.makeAttempts();
    }
}
