package com.prakriti.colorapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SaveImage {

    public static File saveFile(Activity activity, Bitmap bitmap) throws IOException { // use throws to indicate that this method is not safe
        String externalStorageState = Environment.getExternalStorageState(); // checks if external storage exists or not
        File file = null;
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) { // external storage is available for use
            File picturesDirectory = activity.getExternalFilesDir("ColorAppImages"); // saves images as new file to this folder
            // each image must have a unique name
            Date currentDate = new Date(); // gets current date
            long elapsedTime = SystemClock.elapsedRealtime(); // return time since system was booted
            String uniqueImageName = "/" + currentDate + "_" + elapsedTime + ".png";

            file = new File(picturesDirectory + uniqueImageName);
            // write to external storage -> check for free space
            long remFreeSpace = picturesDirectory.getFreeSpace();
            long requiredSpace = bitmap.getByteCount(); // req byte space
            if((requiredSpace * 1.8) < remFreeSpace) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    boolean isImageSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // true if saved successfully
                    if(isImageSaved) {
                        return file;
                    }
                    else {
                        throw new IOException("Error: Unable to save image");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Error occured while saving file\nPlease try again");
                }
            }
            else {
                throw new IOException("Not enough storage space to save file");
            }
        }
        else {
            throw new IOException("This device does not have an external storage");
        }
    }

}
