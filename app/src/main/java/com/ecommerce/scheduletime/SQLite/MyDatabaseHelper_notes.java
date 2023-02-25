package com.ecommerce.scheduletime.SQLite;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MyDatabaseHelper_notes extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "my_notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ID_ = "_id_";
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
                COLUMN_ID_ + " TEXT, " +
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

    private String generateNewId() {

        List<String> primaryKeys = new ArrayList<>();
        MyDatabaseHelper_notes myDB_note = new MyDatabaseHelper_notes(context);
        Cursor categoryCursor = myDB_note.readAllData();
        if (categoryCursor.getCount() == 0) {
            primaryKeys.add("0");
        } else {
            while (categoryCursor.moveToNext()) {
                primaryKeys.add(categoryCursor.getString(1));
            }
        }

        String newId;
        boolean idExists;

        do {
            // Generate a random ID
            newId = UUID.randomUUID().toString();

            // Check if the ID already exists in the list
            idExists = false;
            for (String id : primaryKeys) {
                if (id.equals(newId)) {
                    idExists = true;
                    break;
                }
            }
        } while (idExists);


        return newId;
    }

    public void addBook1(String title, String description, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String new_id = generateNewId();
        cv.put(COLUMN_ID_, new_id);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_TIME, time);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add_note), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

            /** @param add_insert to {@link NewNotes} */

            SharedPreferences prefs = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
            boolean change = prefs.getBoolean("change", false);
            if (change) {
                NewNotes newNotes = new NewNotes(context);
                newNotes.addChange(new_id, title, description, time, "insert");
            }
        }

    }

    // We use this method when retrieve data from firebase with the original id and store them on our database
    public void addBook2(String _id_, String title, String description, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, _id_);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_TIME, time);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add_note), Toast.LENGTH_SHORT).show();
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

    public Cursor readData(String row_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        //get columns of signal data
        //String[] columns = {COLUMN_TITLE,COLUMN_AUTHOR,COLUMN_PAGES};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{row_id}, null, null, null, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String _id_, String title, String description, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, _id_);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_TIME, time);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_update_note), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();

            /** @param add_update to {@link NewNotes} */

                NewNotes newNotes = new NewNotes(context);
                newNotes.addChange(_id_, title, description, time, "update");
        }
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get data before delete.
        String _id_ = null, title = null, description = null, time = null;
        MyDatabaseHelper_notes myDB_notes = new MyDatabaseHelper_notes(context);
        Cursor cursor = myDB_notes.readData(row_id);
        while (cursor.moveToNext()) {
            _id_ = cursor.getString(1);
            title = cursor.getString(2);
            description = cursor.getString(3);
            time = cursor.getString(4);
        }

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_delete), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();

            /** @param add_delete to {@link NewNotes} */

            if (_id_ != null || title != null || description != null || time != null) {
                NewNotes newNotes = new NewNotes(context);
                newNotes.addChange(_id_, title, description, time, "delete");
            }

        }
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
