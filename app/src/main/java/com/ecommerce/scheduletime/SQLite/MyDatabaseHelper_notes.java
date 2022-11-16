package com.ecommerce.scheduletime.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper_notes extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "my_notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "note_title";
    private static final String COLUMN_DESCRIPTION = "note_description";
    private static final String COLUMN_TIME = "note_time";

    public MyDatabaseHelper_notes(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_TIME + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addBook(String title, String description, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_TIME, time);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed Add Note!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }

    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor readData(String row_id){
        SQLiteDatabase db = this.getReadableDatabase();
        //get columns of signal data
        //String[] columns = {COLUMN_TITLE,COLUMN_AUTHOR,COLUMN_PAGES};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{row_id}, null, null, null, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String title, String description, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_TIME, time);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed Update Note!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed Delete Note!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
