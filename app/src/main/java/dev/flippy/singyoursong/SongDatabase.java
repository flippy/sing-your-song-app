package dev.flippy.singyoursong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongDatabase {
    private static final String TAG = "SongDatabase";

    public static final String COL_TITLE = "Title";
    public static final String COL_ARTIST = "Artist";
    public static final String COL_CDTYPE = "CDType";
    public static final String COL_ID = "ID";
    public static final String COL_LIST = "List";

    private static final String DATABASE_NAME = "SingYourSong";
    private static final String TABLE_NAME_SONGS = "Songs";
    private static final String TABLE_NAME_LISTS = "Lists";
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
        String count = "SELECT count(*) FROM " + TABLE_NAME_SONGS;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        return (icount == 0);
    }

    public void setSongs(ArrayList<HashMap<String, String>> songs) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        db.beginTransaction();

        if (songs.size() > 0) {
            // Clear the table first.
            databaseOpenHelper.clearSongs();

            // Add the new song set.
            for (HashMap<String, String> song : songs) {
                databaseOpenHelper.addSong(song.get("id"), song.get("title"), song.get("artist"), song.get("cdtype"), Integer.parseInt(song.get("list")));
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor getSongMatches(SongQuery query) {
        SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
        List<String> whereClauses = new ArrayList<>();
        List<String> whereParameters = new ArrayList<>();
        if (query.getArtist() != null) {
            whereClauses.add("Artist = ?");
            whereParameters.add(query.getArtist());
        }
        if (query.getList() != 0) {
            whereClauses.add("List = ?");
            whereParameters.add(Integer.toString(query.getList()));
        }
        if (query.getSearchText() != null && query.getSearchText().length() > 0) {
            String searchQueryString = "%" + query.getSearchText() + "%";
            whereClauses.add("(Title LIKE ? OR Artist LIKE ?)");
            whereParameters.add(searchQueryString);
            whereParameters.add(searchQueryString);
        }

        return database.query(
                TABLE_NAME_SONGS /* table */,
                new String[] { "*" } /* columns */,
                TextUtils.join(" AND ",whereClauses) /* where or selection */,
                whereParameters.toArray(new String[0]) /* selectionArgs i.e. value to replace ? */,
                null /* groupBy */,
                null /* having */,
                "Title" /* orderBy */
        );
    }

    public Cursor getArtistMatches(SongQuery query) {
        SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
        List<String> whereClauses = new ArrayList<>();
        List<String> whereParameters = new ArrayList<>();
        if (query.getList() != 0) {
            whereClauses.add("List = ?");
            whereParameters.add(Integer.toString(query.getList()));
        }
        if (query.getSearchText() != null && query.getSearchText().length() > 0) {
            String searchQueryString = "%" + query.getSearchText() + "%";
            whereClauses.add("(Title LIKE ? OR Artist LIKE ?)");
            whereParameters.add(searchQueryString);
            whereParameters.add(searchQueryString);
        }

        return database.query(
                TABLE_NAME_SONGS /* table */,
                new String[] { "Artist", "COUNT(*) AS SongCount" } /* columns */,
                TextUtils.join(" AND ",whereClauses) /* where or selection */,
                whereParameters.toArray(new String[0]) /* selectionArgs i.e. value to replace ? */,
                "Artist" /* groupBy */,
                null /* having */,
                "Artist" /* orderBy */
        );
    }

    public void setLists(HashMap<String, String> lists) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        db.beginTransaction();

        if (lists.size() > 0) {
            // Clear the table first.
            databaseOpenHelper.clearLists();

            // Add the new list.
            for (HashMap.Entry<String, String> entry : lists.entrySet()) {
                databaseOpenHelper.addList(Integer.parseInt(entry.getKey()), entry.getValue());
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor getLists() {
        SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_LISTS;
        return database.rawQuery(selectQuery, null);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase mDatabase;

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL("CREATE  TABLE " + TABLE_NAME_SONGS + " (" +
                    COL_ID + " TEXT, " +
                    COL_TITLE + " TEXT, " +
                    COL_ARTIST + " TEXT, " +
                    COL_CDTYPE + " TEXT, " +
                    COL_LIST + " INTEGER)");

            mDatabase.execSQL("CREATE  TABLE " + TABLE_NAME_LISTS + " (" +
                    COL_ID + " INTEGER, " +
                    COL_TITLE + " TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SONGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LISTS);
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
            return database.insert(TABLE_NAME_SONGS, null, initialValues);
        }

        public long addList(Integer id, String title) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_ID, id);
            initialValues.put(COL_TITLE, title);

            SQLiteDatabase database = getWritableDatabase();
            return database.insert(TABLE_NAME_LISTS, null, initialValues);
        }

        public void clearSongs() {
            SQLiteDatabase database = getWritableDatabase();
            database.delete(TABLE_NAME_SONGS, null, null);
        }

        public void clearLists() {
            SQLiteDatabase database = getWritableDatabase();
            database.delete(TABLE_NAME_LISTS, null, null);
        }
    }

    public static class SongQuery {
        private String text = null;
        private int list = 0;
        private String artist = null;

        public String getSearchText() {
            return this.text;
        }

        public void setSearchText(String searchText) {
            this.text = searchText;
        }

        public int getList() {
            return this.list;
        }

        public void setList(int list) {
            this.list = list;
        }

        public String getArtist() {
            return this.artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }
    }
}
