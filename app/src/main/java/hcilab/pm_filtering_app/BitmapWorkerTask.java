package hcilab.pm_filtering_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by alonpek on 2/17/17.
 */
public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

    WeakReference<ImageView> imageViewReferences;
    final static int TARGET_IMAGE_VIEW_WIDTH = 300;
    final static int TARGET_IMAGE_VIEW_HEIGHT = 300;
    private File mImageFile;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewReferences = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(File... params) {
        //return BitmapFactory.decodeFile(params[0].getAbsolutePath());
        mImageFile = params[0];
        //return decodeBitmapFromFile(params[0]);
        Bitmap bitmap = decodeBitmapFromFile(mImageFile);
        CameraIntentActivity.setBitmapToMemoryCache(mImageFile.getName(), bitmap);
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        /*
        if (bitmap != null && imageViewReferences != null) {
            ImageView viewImage = imageViewReferences.get();
            if (viewImage != null) {
                viewImage.setImageBitmap(bitmap);
            }
        }
        */
        if(isCancelled()) {
             bitmap = null;
        }
        if (bitmap != null && imageViewReferences != null) {
            ImageView imageView = imageViewReferences.get();
            BitmapWorkerTask bitmapWorkerTask = ImageAdapter.getBitMapWorkerTast(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }

    private int calculateSampleSize(BitmapFactory.Options bmOptions) {
        final int width = bmOptions.outWidth;
        final int height = bmOptions.outHeight;
        int scaleFactor = 1;

        if (width > TARGET_IMAGE_VIEW_WIDTH || height > TARGET_IMAGE_VIEW_HEIGHT) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            while (halfWidth/scaleFactor > TARGET_IMAGE_VIEW_WIDTH
                    || halfHeight/scaleFactor > TARGET_IMAGE_VIEW_HEIGHT) {
                scaleFactor *= 2;
            }
        }
        return scaleFactor;
    }

    private Bitmap decodeBitmapFromFile(File imageFile) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //Turns on "dummy" read mode
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
        bmOptions.inSampleSize = calculateSampleSize(bmOptions);
        //Turns off "dummy" read mode, actually loads Bitmap
        bmOptions.inJustDecodeBounds = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            addInBitmapOptions(bmOptions);
        }
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
    }

    public File getImageFile() {
        return mImageFile;
    }

    private static void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;
        Bitmap bitmap = CameraIntentActivity.getBitmapFromReusableSet(options);
        if(bitmap != null){
            options.inBitmap = bitmap;
        }
    }




}
