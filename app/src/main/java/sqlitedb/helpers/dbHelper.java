package sqlitedb.helpers;

/**
 * dbHelper.java
 * Helper class encapsulating SQLite operations for the photo tables.
 * Created by DED8IRD on 11/7/2016.
 */

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

    // Pip Session Info Table - attributes
    private static final String KEY_PARTICIPANT = "participant";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_RANK = "rank";
    private static final String KEY_TAG = "tag";

    // Table Create Statements
    private static final String CREATE_TABLE_PHOTOS = "CREATE TABLE "
            + TABLE_PHOTOS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PARTICIPANT + " CHAR(40), "
            + KEY_TIMESTAMP + " CHAR(60), "
            + KEY_IMAGE + " TEXT, "
            + KEY_RANK + " INTEGER, "
            + KEY_TAG + " CHAR(40)" + ")";

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

    // ------------------------ "Pip Sessions" table methods ------------------------ //

    /*
     * Creating a Pip Session
     */
    public long addPhoto(Photo session) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PARTICIPANT, session.getParticipant());
        values.put(KEY_TIMESTAMP, session.getTimestamp());
        values.put(KEY_IMAGE, session.getImage());
        values.put(KEY_RANK, session.getRank());
        values.put(KEY_TAG, session.getTag());

        // insert row
        return db.insert(TABLE_PHOTOS, null, values);
    }

    /*
     * get single session
     */
    public Photo getSession(long session_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PHOTOS + " WHERE "
                + KEY_ID + " = " + session_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Photo session = new Photo();
        session.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        session.setParticipant((c.getString(c.getColumnIndex(KEY_PARTICIPANT))));

        return session;
    }

    /*
     * getting all sessions
     */
    public List<Photo> getAllSessions() {
        List<Photo> sessions = new ArrayList<Photo>();
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTOS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Photo session = new Photo();
                session.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                session.setParticipant((c.getString(c.getColumnIndex(KEY_PARTICIPANT))));
                session.setTimestamp((c.getString(c.getColumnIndex(KEY_TIMESTAMP))));
                session.setImage((c.getString(c.getColumnIndex(KEY_IMAGE))));
                session.setRank((c.getInt(c.getColumnIndex(KEY_RANK))));
                session.setTag((c.getString(c.getColumnIndex(KEY_TAG))));

                // adding to session list
                sessions.add(session);
            } while (c.moveToNext());
        }

        return sessions;
    }

    /*
     * Get session count
     */
    public int getSessionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PHOTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Updating a session
     */
    public int updateSession(Photo session) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, session.getId());
        values.put(KEY_PARTICIPANT, session.getParticipant());
        values.put(KEY_TIMESTAMP, session.getTimestamp());
        values.put(KEY_IMAGE, session.getImage());
        values.put(KEY_RANK, session.getRank());
        values.put(KEY_TAG, session.getTag());

        // updating row
        return db.update(TABLE_PHOTOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(session.getId()) });
    }

    /*
     * Deleting a session
     */
    public void deleteSession(long session_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTOS, KEY_ID + " = ?",
                new String[] { String.valueOf(session_id) });
    }

    /*
     * Clear session
     */
    public void clearSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

}
