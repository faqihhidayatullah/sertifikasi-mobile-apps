package com.example.product_bottomnav;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.database.Cursor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static String getPath(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        if (fileName == null) return null;

        File file = new File(context.getCacheDir(), fileName);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
