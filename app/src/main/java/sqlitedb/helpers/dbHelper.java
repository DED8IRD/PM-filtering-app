package sqlitedb.helpers;

/**
 * dbHelper.java
 * Helper class encapsulating SQLite operations for the photos table.
 * Created by DED8IRD on 11/7/2016.
 */

import android.animation.PropertyValuesHolder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import sqlitedb.models.Photo;


public class dbHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = dbHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Photo-db";

    // Table Names
    private static final String TABLE_PHOTOS = "Photos";

    // Common column names
    private static final String KEY_ID = "id";

    // Photo Info Table - attributes
    private static final String KEY_PARTICIPANT = "participant";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TAG = "tag";
    private static final String KEY_RANK = "rank";
    private  static final String KEY_DELETE = "delete";

    // Table Create Statements
    private static final String CREATE_TABLE_PHOTOS = "CREATE TABLE "
            + TABLE_PHOTOS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PARTICIPANT + " CHAR(40), "
            + KEY_TIMESTAMP + " CHAR(60), "
            + KEY_IMAGE + " TEXT, "
            + KEY_TAG + " CHAR(40),"
            + KEY_RANK + " INTEGER, "
            + KEY_DELETE + "CHAR(60)" +")";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_PHOTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);

        // create new tables
        onCreate(db);
    }

    // ------------------------ Photo table methods ------------------------ //

    /*
     * Add photo to table
     */
    public long addPhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PARTICIPANT, photo.getParticipant());
        values.put(KEY_TIMESTAMP, photo.getTimestamp());
        values.put(KEY_IMAGE, photo.getImage());
        values.put(KEY_TAG, photo.getTag());
        values.put(KEY_RANK, photo.getRank());
        values.put(KEY_DELETE, photo.getDelete());

        // insert row
        return db.insert(TABLE_PHOTOS, null, values);
    }

    /*
     * Get single photo
     */
    public Photo getPhoto(long photo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PHOTOS + " WHERE "
                + KEY_ID + " = " + photo_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Photo photo = new Photo();
        photo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        photo.setParticipant((c.getString(c.getColumnIndex(KEY_PARTICIPANT))));

        return photo;
    }

    /*
     * Get all photos
     */
    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<Photo>();
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTOS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                photo.setParticipant(c.getString(c.getColumnIndex(KEY_PARTICIPANT)));
                photo.setTimestamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                photo.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
                photo.setTag(c.getString(c.getColumnIndex(KEY_TAG)));
                photo.setRank(c.getInt(c.getColumnIndex(KEY_RANK)));
                photo.setDelete(c.getString(c.getColumnIndex(KEY_DELETE)));

                // adding to photo list
                photos.add(photo);
            } while (c.moveToNext());
        }

        return photos;
    }

    /*
     * Get photo count
     */
    public int getPhotoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PHOTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Update photo
     */
    public int updatePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, photo.getId());
        values.put(KEY_PARTICIPANT, photo.getParticipant());
        values.put(KEY_TIMESTAMP, photo.getTimestamp());
        values.put(KEY_IMAGE, photo.getImage());
        values.put(KEY_TAG, photo.getTag());
        values.put(KEY_RANK, photo.getRank());
        values.put(KEY_DELETE, photo.getDelete());

        // updating row
        return db.update(TABLE_PHOTOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(photo.getId()) });
    }

    /*
     * Update photo rank
     */
    public int updateTag(Photo photo, int rank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, photo.getId());
        values.put(KEY_PARTICIPANT, photo.getParticipant());
        values.put(KEY_TIMESTAMP, photo.getTimestamp());
        values.put(KEY_IMAGE, photo.getImage());
        values.put(KEY_TAG, photo.getTag());
        values.put(KEY_RANK, rank);
        values.put(KEY_DELETE, photo.getDelete());

        // updating row
        return db.update(TABLE_PHOTOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(photo.getId()) });
    }

    /*
    * Update photo tag
    */
    public int updateTag(Photo photo, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, photo.getId());
        values.put(KEY_PARTICIPANT, photo.getParticipant());
        values.put(KEY_TIMESTAMP, photo.getTimestamp());
        values.put(KEY_IMAGE, photo.getImage());
        values.put(KEY_TAG, tag);
        values.put(KEY_RANK, photo.getRank());
        values.put(KEY_DELETE, photo.getDelete());

        // updating row
        return db.update(TABLE_PHOTOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(photo.getId()) });
    }

    /*
     * Delete photo
     * Note: Does NOT delete photo from table
     * This method adds a timestamp to KEY_DELETE
     */
    public int deletePhoto(Photo photo, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, photo.getId());
        values.put(KEY_PARTICIPANT, photo.getParticipant());
        values.put(KEY_TIMESTAMP, photo.getTimestamp());
        values.put(KEY_IMAGE, photo.getImage());
        values.put(KEY_TAG, photo.getTag());
        values.put(KEY_RANK, photo.getRank());
        values.put(KEY_DELETE, timestamp);

        // updating row
        return db.update(TABLE_PHOTOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(photo.getId()) });
    }

    /*
     * Clear table
     */
    public void clearPhoto() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

}
