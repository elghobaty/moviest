package com.elghobaty.moviest.utility;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

public class PicassoHelper {

    public static void loadPoster(Context context, String imagePath, View view) {
        RequestCreator picassoLoader = picassoLoader(context, imagePath);
        picassoLoader.into((ImageView) view);
    }

    private static RequestCreator picassoLoader(Context context, String imagePath) {
        Picasso picasso = Picasso.with(context);
        if (FileHelper.isRemotePath(imagePath)) {
            return picasso.load(imagePath);
        }

        return picasso.load(new File(imagePath));
    }
}
