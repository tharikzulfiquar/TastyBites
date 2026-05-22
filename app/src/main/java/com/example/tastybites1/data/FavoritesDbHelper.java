package com.example.tastybites1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tastybites1.Recipe;  // Tweaked: Import from main

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tastybites.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "favorites";
    public static final String COL_ID = "_id"; // local row id
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_TITLE = "title";
    public static final String COL_READY_MINUTES = "ready_minutes";  // Tweaked: Replaced COL_DESC
    public static final String COL_IMAGE = "image_url";

    // https://developer.android.com/training/data-storage/sqlite  // Acknowledged source
    private static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RECIPE_ID + " INTEGER UNIQUE, " +
                    COL_TITLE + " TEXT, " +
                    COL_READY_MINUTES + " INTEGER, " +  // Tweaked
                    COL_IMAGE + " TEXT" +
                    ")";

    public FavoritesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long addFavorite(Recipe r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_ID, r.getId());
        cv.put(COL_TITLE, r.getTitle());
        cv.put(COL_READY_MINUTES, r.getReadyInMinutes());  // Tweaked
        cv.put(COL_IMAGE, r.getImage());
        return db.insertWithOnConflict(TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long addFavoriteIdOnly(int recipeId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_ID, recipeId);
        return db.insertWithOnConflict(TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean removeFavorite(int recipeId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE, COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        return rows > 0;
    }

    public boolean isFavorite(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, new String[]{COL_RECIPE_ID}, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        boolean found = c.moveToFirst();
        c.close();
        return found;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        boolean empty = true;
        if (c.moveToFirst()) {
            empty = c.getInt(0) == 0;
        }
        c.close();
        return empty;
    }

    public void seedSampleIdsIfEmpty(int[] sampleIds) {
        if (!isEmpty()) return;
        for (int id : sampleIds) {
            addFavoriteIdOnly(id);
        }
    }

    public void seedSamplesIfEmpty(List<Recipe> samples) {
        if (!isEmpty()) return;
        if (samples == null) return;
        for (Recipe r : samples) {
            addFavorite(r);
        }
    }

    public List<Integer> getAllFavoriteIds() {
        SQLiteDatabase db = getReadableDatabase();
        List<Integer> list = new ArrayList<>();
        Cursor c = db.query(TABLE, new String[]{COL_RECIPE_ID}, null, null, null, null, COL_ID + " DESC");
        while (c.moveToNext()) {
            list.add(c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID)));
        }
        c.close();
        return list;
    }

    public Map<Integer, String> getFavoriteTitlesMap() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Integer, String> map = new HashMap<>();
        Cursor c = db.query(TABLE, new String[]{COL_RECIPE_ID, COL_TITLE}, null, null, null, null, null);
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID));
            String title = c.getString(c.getColumnIndexOrThrow(COL_TITLE));
            map.put(id, title);
        }
        c.close();
        return map;
    }

    public List<Recipe> getAllFavorites() {
        SQLiteDatabase db = getReadableDatabase();
        List<Recipe> list = new ArrayList<>();
        Cursor c = db.query(TABLE, null, null, null, null, null, COL_ID + " DESC");
        while (c.moveToNext()) {
            int recipeId = c.getInt(c.getColumnIndexOrThrow(COL_RECIPE_ID));
            String title = c.getString(c.getColumnIndexOrThrow(COL_TITLE));
            int minutes = c.getInt(c.getColumnIndexOrThrow(COL_READY_MINUTES));  // Tweaked
            String image = c.getString(c.getColumnIndexOrThrow(COL_IMAGE));
            list.add(new Recipe(recipeId, title, minutes, image));
        }
        c.close();
        return list;
    }
}