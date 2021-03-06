package hcilab.pm_filtering_app;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sqlitedb.helpers.DBHelper;
import sqlitedb.models.Photo;



/**
 * Created by alonpek on 2/14/17.
 */
public class CameraIntentActivity extends Activity {
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = null;
    private String GALLERY_LOCATION = null;
    private File mGalleryFolder;
    private static LruCache<String, Bitmap> memoryCache;
    private static Set<SoftReference<Bitmap>> reusableBitmap;
    private DBHelper db;
    private RecyclerView mRecyclerView;
    private static Context context;
    private int rating;
    private int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_intent);
        CameraIntentActivity.context = getApplicationContext();
        //Pass participant ID from login screen as gallery location
        Bundle bundle = getIntent().getExtras();
        GALLERY_LOCATION = bundle.getString("send_participant");
        // Create gallery
        createImageGallery();
//      // Initialize db
        db = new DBHelper(CameraIntentActivity.context);
        mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        //Int at end = number of columns in recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter imageAdapter = new ImageAdapter(mGalleryFolder, this);
        mRecyclerView.setAdapter(imageAdapter);


        //Set up memory cache
        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemorySize / 100;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    reusableBitmap.add(new SoftReference<Bitmap>(oldValue));
                }
            }


        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            reusableBitmap = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

        //Intent intent = new Intent(CameraIntentActivity.this, ImageAdapter.class);
        //intent.putExtra("mainContext", CameraIntentActivity.context);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePhoto(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            // Toast.makeText(this, "Picture taken successfully", Toast.LENGTH_SHORT).show();
            // Bundle extras = data.getExtras();
            // Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
            // mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
            // Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            // mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
            //setReducedImageSize();
            RecyclerView.Adapter newImageAdapter = new ImageAdapter(mGalleryFolder, this);
            mRecyclerView.swapAdapter(newImageAdapter, false);

        }

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            rating = Integer.parseInt(data.getStringExtra("key_rating"));
        }
    }

    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if (!mGalleryFolder.exists()) {
            mGalleryFolder.mkdirs();
        }
    }


    File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName,".jpg", mGalleryFolder);
        mImageFileLocation = image.getAbsolutePath();

        // Add photo to db
        Photo photo = new Photo(GALLERY_LOCATION, timeStamp, mImageFileLocation);
        db.addPhoto(photo);


        //Update rank of photo
        //int rank = -1;


        Intent i = new Intent(getApplicationContext(), RatingActivity.class);
        startActivityForResult(i, 0);




        db.updateRank(photo, rating);

        Log.d("Get photos", "Getting all photos from db");
        List<Photo> allPhotos = db.getAllPhotos();
        for(Photo p: allPhotos) {
            Log.d("id", String.valueOf(p.getId()));
            Log.d("participant", p.getParticipant());
            Log.d("timestamp", p.getTimestamp());
            Log.d("location", p.getImage());
            Log.d("tag", String.valueOf(p.getTag()));
            Log.d("rank", String.valueOf(p.getRank()));
            Log.d("delete", String.valueOf(p.getDelete()));
        }

        return image;

    }


    void setReducedImageSize() {
        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);


    }

    public static Bitmap getBitmapFromMemoryCache(String key) {
        return memoryCache.get(key);
    }

    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {
        //if (getBitmapFromMemoryCache(key) == null) {
        if(getBitmapFromMemoryCache(key) != null){
            memoryCache.put(key, bitmap);
        }
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    private static boolean canUseForBitmap(Bitmap candidate, BitmapFactory.Options options) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int width = options.outWidth / options.inSampleSize;
            int height = options.outHeight / options.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }
        return candidate.getWidth() == options.outWidth &&
                candidate.getHeight() == options.outHeight &&
                options.inSampleSize == 1;
    }

    public static Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        if(reusableBitmap != null && !reusableBitmap.isEmpty()){
            synchronized (reusableBitmap) {
                Bitmap item;
                Iterator<SoftReference<Bitmap>> iterator = reusableBitmap.iterator();
                while(iterator.hasNext()) {
                    item = iterator.next().get();
                    if(item != null && item.isMutable()) {
                        if(canUseForBitmap(item, options)) {
                            bitmap = item;
                            iterator.remove();
                            break;
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    public static Context getMainContext() {
        return CameraIntentActivity.context;
    }

}
