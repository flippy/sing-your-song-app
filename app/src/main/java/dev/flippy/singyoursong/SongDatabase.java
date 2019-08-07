package dev.flippy.singyoursong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SongDatabase {
    private static final String TAG = "SongDatabase";

    public static final String COL_TITLE = "Title";
    public static final String COL_ARTIST = "Artist";
    public static final String COL_CDTYPE = "CDType";
    public static final String COL_ID = "ID";
    public static final String COL_LIST = "List";

    private static final String DATABASE_NAME = "SingYourSong";
    private static final String TABLE_NAME = "Songs";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper databaseOpenHelper;

    public SongDatabase(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context);
    }

    /*public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_TITLE + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }*/

    public boolean isEmpty() {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        return (icount == 0);
    }

    public void setSongs(ArrayList<HashMap<String, String>> songs) {
        Log.e(TAG, "setSongs triggered!");
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        db.beginTransaction();

        for (HashMap<String, String> song : songs) {
            databaseOpenHelper.addSong(song.get("id"), song.get("title"), song.get("artist"), song.get("cdtype"), Integer.parseInt(song.get("list")));
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor getSongMatches(String query, String[] columns) {
        //String selection = COL_TITLE + " LIKE ?";
        //String[] selectionArgs = new String[] {query+"*"};

        SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(selectQuery, null);
        //return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);

        Cursor cursor = builder.query(databaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        /*if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }*/
        return cursor;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase mDatabase;

        private static final String TABLE_CREATE =
                "CREATE  TABLE " + TABLE_NAME + " (" +
                        COL_ID + " TEXT, " +
                        COL_TITLE + " TEXT, " +
                        COL_ARTIST + " TEXT, " +
                        COL_CDTYPE + " TEXT, " +
                        COL_LIST + " INTEGER)";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e(TAG, "before create");
            mDatabase = db;
            mDatabase.execSQL(TABLE_CREATE);
            Log.e(TAG, "after create");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public long addSong(String id, String title, String artist, String cdType, Integer list) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_ID, id);
            initialValues.put(COL_TITLE, title);
            initialValues.put(COL_ARTIST, artist);
            initialValues.put(COL_CDTYPE, cdType);
            initialValues.put(COL_LIST, list);

            SQLiteDatabase database = getWritableDatabase();
            return database.insert(TABLE_NAME, null, initialValues);
        }
    }
}
