package com.example.hikemate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hikemate.model.HikeSpot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hikespots.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "hikespots";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLACE = "place";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "long";
    private static final String COLUMN_CHAT_ID = "chat_id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PLACE + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LNG + " TEXT, " +
                COLUMN_CHAT_ID + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertHikeSpot(HikeSpot hikeSpot) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("InsertHikeSpot", "Inserting ID: " + hikeSpot.getId());

        values.put(COLUMN_ID, hikeSpot.getId());
        values.put(COLUMN_PLACE, hikeSpot.getPlace());
        values.put(COLUMN_LAT, hikeSpot.getLat());
        values.put(COLUMN_LNG, hikeSpot.getLong());
        values.put(COLUMN_CHAT_ID, hikeSpot.getChat_id());
        values.put(COLUMN_PHONE_NUMBER, hikeSpot.getPhone_number());

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public List<HikeSpot> getAllHikeSpots() {
        List<HikeSpot> hikeSpots = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                HikeSpot hikeSpot = new HikeSpot();
                hikeSpot.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                hikeSpot.setPlace(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE)));
                hikeSpot.setLat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT)));
                hikeSpot.setLong(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LNG)));
                hikeSpot.setChat_id(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID)));
                hikeSpot.setPhone_number(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)));
                hikeSpots.add(hikeSpot);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return hikeSpots;
    }

}

