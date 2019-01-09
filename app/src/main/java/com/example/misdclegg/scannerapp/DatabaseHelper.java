package com.example.misdclegg.scannerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "parts_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "description";
    private static final String COL3 = "classification";
    private static final String COL4 = "condition";
    private static final String COL5 = "user";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 + " TEXT PRIMARY KEY, " +
                COL2 +" TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Bundle myBundle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, myBundle.getString("ID", ""));
        contentValues.put(COL2, myBundle.getString("DESCRIPTION", ""));
        contentValues.put(COL3, myBundle.getString("CLASSIFICATION", ""));
        contentValues.put(COL4, myBundle.getString("CONDITION", ""));
        contentValues.put(COL5, myBundle.getString("USER", ""));

                //Log.d(TAG, "addData: Adding " + mId + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemDescript(String serial){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + ", " + COL3 + ", " + COL4 + ", " +
                 COL5 +
                " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + serial + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemCondition(String serial){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL4 +
                " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + serial + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateCondition(String newCondition, String newUser, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL4 +
                " = '" + newCondition + "', " +
                COL5 +
                " = '" + newUser +
                "' WHERE " + COL1 + " = '" + id + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newCondition);
        db.execSQL(query);
    }
}
