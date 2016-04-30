package com.elghobaty.moviest.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.elghobaty.moviest.data.utility.FavouredMovie;

import java.io.FileOutputStream;
import java.io.IOException;

public class PersistRemoteImage extends AsyncTask<Object, Void, String> {
    private final FavouredMovie movieHandler;

    public PersistRemoteImage(FavouredMovie favouredMovie) {
        movieHandler = favouredMovie;
    }

    @Override
    protected void onPostExecute(String path) {
        if (path != null) {
            movieHandler.updateMoviePoster(path);
        }
    }

    @Override
    protected String doInBackground(Object... params) {
        FileOutputStream fileOutputStream = null;
        Bitmap bitmap = (Bitmap) params[0];
        String fileExtension = (String) params[2];
        String fileName = params[1] + "." + fileExtension;
        String path = null;
        try {
            fileOutputStream = movieHandler.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            Bitmap.CompressFormat format;
            switch (fileExtension.toLowerCase()) {
                case "png":
                    format = Bitmap.CompressFormat.PNG;
                    break;
                case "jpg":
                case "jpeg":
                    format = Bitmap.CompressFormat.JPEG;
                    break;

                default:
                    throw new RuntimeException("Image extension: " + fileExtension + " is not supported.");
            }

            bitmap.compress(format, 100, fileOutputStream);
            path = movieHandler.getActivity().getFilesDir() + "/" + fileName;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return path;
    }
}
