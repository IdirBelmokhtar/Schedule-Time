package com.ecommerce.scheduletime.SQLite;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.R;

public class NewTasks extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "NewTask.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_NAME = "my_new_Tasks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ID_ = "_id_";
    private static final String COLUMN_DATE = "task_date";
    private static final String COLUMN_TITLE = "task_title";
    private static final String COLUMN_DESCRIPTION = "task_description";
    private static final String COLUMN_PRIORITY = "task_priority";
    private static final String COLUMN_CATEGORY = "task_category";
    private static final String COLUMN_TIME = "task_time";
    private static final String COLUMN_DONE = "task_done";
    private static final String COLUMN_REMINDER = "task_reminder";
    private static final String COLUMN_CONDITION = "condition";

    public NewTasks(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_ + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_DONE + " TEXT, " +
                COLUMN_REMINDER + " INTEGER, " +
                COLUMN_CONDITION + " TEXT, " +
                "CHECK (" + COLUMN_CONDITION + " IN ('insert','update','delete')));";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //db.execSQL("DROP TRIGGER IF EXISTS " + TRIGGER_NAME);
        onCreate(db);
    }

    public void addChange(String _id_, String date, String title, String description, String priority, String category, String time, String done, String reminder, String condition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Create new_note_table after getting data from firebase in first time.
        SharedPreferences prefs = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
        boolean change = prefs.getBoolean("change", false);
        if (change) {
            cv.put(COLUMN_ID_, _id_);
            cv.put(COLUMN_DATE, date);
            cv.put(COLUMN_TITLE, title);
            cv.put(COLUMN_DESCRIPTION, description);
            cv.put(COLUMN_PRIORITY, priority);
            cv.put(COLUMN_CATEGORY, category);
            cv.put(COLUMN_TIME, time);
            cv.put(COLUMN_DONE, done);
            cv.put(COLUMN_REMINDER, reminder);
            cv.put(COLUMN_CONDITION, condition);

            long result = db.insert(TABLE_NAME, null, cv);
            if (result == -1) {
                Toast.makeText(context, context.getResources().getString(R.string.failed_add_category), Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_delete), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

}
