package com.example.webservice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Rakshith on 8/6/2015.
 */
public class Utilities {

    public static File convertInToReqiuredOrientation(Activity activity,File file) throws IOException {
        try {
            String Path = activity.getFilesDir().getAbsolutePath() + "/"+file.getName();

            File f = new File(Path);
            copyFile(file, f);

            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
            // System.out.println("convertInToReqiuredOrientation");
            Matrix mat = new Matrix();
            mat.postRotate(angle);
            Bitmap correctBmp = null;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(f),null, null);
            correctBmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(),
                    bmp1.getHeight(), mat, true);
            FileOutputStream fOut = new FileOutputStream(f.getAbsolutePath());
            correctBmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            return f;
            // System.out.println("end convertInToReqiuredOrientation");

        } catch (OutOfMemoryError e) {

        } catch (IOException e) {

        } catch (Exception e) {

        }
        return file;
    }


    public static Boolean copyFile(File sourceFile, File destFile)
            throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            return true;
        }
        return false;
    }



}
