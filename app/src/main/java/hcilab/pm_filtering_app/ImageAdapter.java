package hcilab.pm_filtering_app;
import sqlitedb.helpers.DBHelper;
import sqlitedb.models.Photo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    static HashMap<Integer, String> posTitle = new HashMap<>();

    private File imagesFile;
    private Bitmap placeHolder;
    String curTitle;
    int curId;
    Context context;


    public static class AsyncDrawable extends BitmapDrawable{
        final WeakReference<BitmapWorkerTask> taskReference;

        public AsyncDrawable(Resources resources, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super (resources, bitmap);
            taskReference = new WeakReference(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBipmapWorkerTask() {
            return taskReference.get();
        }
    }

    public ImageAdapter(File folderFile, Context context) {
        imagesFile = folderFile;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ViewHolder holder, int position) {
        final File imageFile = imagesFile.listFiles()[position];
        posTitle.put(position, imageFile.getName());
        //holder.title.setText(galleryList.get(position).getImage_title());
        //Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        //holder.getImageView().setImageBitmap(imageBitmap);
        //BitmapWorkerTask workerTask = new BitmapWorkerTask(holder.getImageView());
        //workerTask.execute(imageFile);
        //Glide.with(holder.getImageView().getContext()).load(imageFile).into(holder.getImageView());


        //Toast.makeText(CameraIntentActivity.getMainContext(), imageFile.getName(), Toast.LENGTH_SHORT).show();


        //curTitle = imageFile.getName();

        Bitmap bitmap = CameraIntentActivity.getBitmapFromMemoryCache(imageFile.getName());
        if (bitmap != null) {
            holder.getImageView().setImageBitmap(bitmap);
        }
        else if (checkBitmapWorkerTask(imageFile, holder.getImageView())) {
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(holder.getImageView());
            AsyncDrawable asyncDrawable = new AsyncDrawable(holder.getImageView().getResources(),
                    placeHolder, bitmapWorkerTask);
            holder.getImageView().setImageDrawable(asyncDrawable);
            bitmapWorkerTask.execute(imageFile);
        }


        /*
        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //holder.getImageView().getId();
                Toast.makeText(CameraIntentActivity.getMainContext(), Integer.toString(getItemCount()), Toast.LENGTH_SHORT).show();
                //curId =holder.getImageView().getId();

                PopupMenu popupMenu = new PopupMenu(holder.getImageView().getContext(), view);
                //PopupMenu popupMenu = new PopupMenu(CameraIntentActivity.getMainContext(), view);
                popupMenu.setOnMenuItemClickListener(ImageAdapter.this);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.my_popup, popupMenu.getMenu());
                popupMenu.show();

            }
        });
        */

    }



    @Override
    public int getItemCount() {
        return imagesFile.listFiles().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMenuItemClickListener {
        private ImageView imageView;
        //private int position = imageView.getMeasuredState();
        private boolean positive = false;
        private boolean negative = false;
        //public TextView title;
        //Context context;



        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.img);
            //this.context = context;
            view.setOnClickListener(this);


        }


        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            String imageFileName = posTitle.get(pos);

            //Toast.makeText(CameraIntentActivity.getMainContext(), Integer.toString(pos), Toast.LENGTH_SHORT).show();
            
            //int position = this.mPosition;

            PopupMenu popupMenu = new PopupMenu(imageView.getContext(), view);
            //PopupMenu popupMenu = new PopupMenu(CameraIntentActivity.getMainContext(), view);
            popupMenu.setOnMenuItemClickListener(this);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.my_popup, popupMenu.getMenu());
            popupMenu.show();

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //Toast.makeText(CameraIntentActivity.getMainContext(), , Toast.LENGTH_SHORT).show();
            int pos = getAdapterPosition();
            String imageFileName = posTitle.get(pos);
            switch(menuItem.getItemId()) {
                case R.id.negative_react:
                    Toast.makeText(CameraIntentActivity.getMainContext(),imageFileName + " is negative react", Toast.LENGTH_SHORT).show();
                    positive = false;
                    negative = true;
                    return true;
                case R.id.positive_react:
                    Toast.makeText(CameraIntentActivity.getMainContext(), imageFileName + " is positive react", Toast.LENGTH_SHORT).show();
                    positive = true;
                    negative = false;
                    return true;
                default:
                    return false;
            }
            //return false;
        }
    }

    public static BitmapWorkerTask getBitMapWorkerTast(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof AsyncDrawable) {
            AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
            return asyncDrawable.getBipmapWorkerTask();
        }
        return null;
    }

    public static boolean checkBitmapWorkerTask(File imageFile, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitMapWorkerTast(imageView);
        if(bitmapWorkerTask != null) {
            final File workerFile = bitmapWorkerTask.getImageFile();
            if (workerFile != null) {
                if (workerFile != imageFile) {
                    bitmapWorkerTask.cancel(true);
                } else {
                    //bitmap worker task file is the same as the imageview is expecting
                    //so do nothing
                    return false;
                }
            }
        }
        return true;
    }

}
