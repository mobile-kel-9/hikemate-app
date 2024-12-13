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

    // Hike spots table
    private static final String TABLE_HIKESPOTS = "hikespots";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLACE = "place";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "long";
    private static final String COLUMN_CHAT_ID = "chat_id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";

    // Temporary proximity alerts table
    private static final String TABLE_TEMP_ALERTS = "temp_proximity_alerts";
    private static final String COLUMN_ALERT_CHAT_ID = "chat_id";
    private static final String COLUMN_ALERT_HIKE_SPOT = "hike_spot_name";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create hike spots table
        String createHikeSpotsTable = "CREATE TABLE " + TABLE_HIKESPOTS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PLACE + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LNG + " TEXT, " +
                COLUMN_CHAT_ID + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT)";
        db.execSQL(createHikeSpotsTable);

        // Create temporary proximity alerts table
        String createTempAlertsTable = "CREATE TABLE " + TABLE_TEMP_ALERTS + " (" +
                COLUMN_ALERT_CHAT_ID + " TEXT NOT NULL, " +
                COLUMN_ALERT_HIKE_SPOT + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTempAlertsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKESPOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_ALERTS);
        onCreate(db);
    }

    // Insert hike spot
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

        db.insertWithOnConflict(TABLE_HIKESPOTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Get all hike spots
    public List<HikeSpot> getAllHikeSpots() {
        List<HikeSpot> hikeSpots = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIKESPOTS, null, null, null, null, null, null);

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

    // Insert temporary proximity alert
    public boolean insertTempProximityAlert(String chatId, String hikeSpotName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALERT_CHAT_ID, chatId);
        values.put(COLUMN_ALERT_HIKE_SPOT, hikeSpotName);

        long result = db.insert(TABLE_TEMP_ALERTS, null, values);

        db.close();
        return result != -1; // Return true if insert was successful
    }

    public List<TemporaryHikeSpot> getTemporaryHikeSpots() {
        List<TemporaryHikeSpot> temporaryHikeSpots = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEMP_ALERTS, null, null, null, null, null, null);

        if (cursor != null) {
            // Check if the cursor has any data
            if (cursor.moveToFirst()) {
                do {
                    // Get the column indices
                    int chatIdIndex = cursor.getColumnIndex(COLUMN_ALERT_CHAT_ID);
                    int hikeSpotNameIndex = cursor.getColumnIndex(COLUMN_ALERT_HIKE_SPOT);

                    // Check if the indices are valid
                    if (chatIdIndex >= 0 && hikeSpotNameIndex >= 0) {
                        String chatId = cursor.getString(chatIdIndex);
                        String hikeSpotName = cursor.getString(hikeSpotNameIndex);
                        temporaryHikeSpots.add(new TemporaryHikeSpot(chatId, hikeSpotName));
                    } else {
                        Log.e("DatabaseHelper", "Column index not found. Chat ID index: " + chatIdIndex + ", Hike Spot Name index: " + hikeSpotNameIndex);
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "Cursor is empty.");
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Cursor is null.");
        }
        return temporaryHikeSpots;
    }

    // Clear all temporary proximity alerts
    public void clearTempProximityAlerts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TEMP_ALERTS);
        Log.d("ProximityAlert", "All temporary proximity alerts cleared.");
        db.close();
    }
}