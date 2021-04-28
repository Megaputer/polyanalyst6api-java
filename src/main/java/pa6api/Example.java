package pa6api;

import java.io.File;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            api = PA6APIImpl.create("https://10.0.0.9:5043", "2495735f836100cd");
            api.login("administrator", "");
            Drive drive = api.drive();
            /*drive.deleteFolder("photo", "");
            drive.createFolder("photo", "");

            drive.uploadFolder("c:/temp/photo", "photo", new ProgressHandler(){
                @Override
                public void onProgress(float perc, long bytesUploaded, long bytesTotal) {
                    if (perc == 100) {
                        System.out.println(uploadedFiles + " of " + totalFiles);
                    }
                }
            });*/
            drive.downloadFile("IMG_20181031_191318.jpg", "photo", "");
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
