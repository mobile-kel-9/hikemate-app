package com.example.hikemate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hikemate.model.HikeSpot;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hikespots.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "hikespots";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLACE = "place";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "lng";
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

        values.put(COLUMN_ID, hikeSpot.getId());
        values.put(COLUMN_PLACE, hikeSpot.getPlace());
        values.put(COLUMN_LAT, hikeSpot.getLat());

        if (hikeSpot.getLng() != null) {
            values.put(COLUMN_LNG, hikeSpot.getLng());
        } else {
            values.putNull(COLUMN_LNG);
        }

        values.put(COLUMN_CHAT_ID, hikeSpot.getChat_id());
        values.put(COLUMN_PHONE_NUMBER, hikeSpot.getPhone_number());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}

