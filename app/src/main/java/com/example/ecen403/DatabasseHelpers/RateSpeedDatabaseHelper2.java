package com.example.ecen403.DatabasseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RateSpeedDatabaseHelper2 extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "rate_Points2.db";
    private static final String TABLE_NAME = "data_points";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "timestamp";
    private static final String COL_3 = "rate_speed";

    private static final int DATABASE_VERSION = 2;

    public RateSpeedDatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp REAL, rate_speed REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Upgrade from version 1 to 2
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db); // Recreate the tables
        }
        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN rate_speed REAL");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Optionally, you could reset the database if this is acceptable:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(double timestamp, double rateSpeed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", timestamp);
        contentValues.put("rate_speed", rateSpeed);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1; // Return true if data inserted successfully
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
