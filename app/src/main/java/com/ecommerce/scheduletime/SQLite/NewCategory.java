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

public class NewCategory extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "NewCategory.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_NAME = "my_new_Tasks_Category";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ID_ = "_id_";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_COLOR = "category_color_Ref";
    private static final String COLUMN_CATEGORY_DELETED = "category_deleted";
    private static final String COLUMN_CONDITION = "condition";

    public NewCategory(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_ + " TEXT, " +
                COLUMN_CATEGORY_NAME + " TEXT, " +
                COLUMN_CATEGORY_COLOR + " INTEGER, " +
                COLUMN_CATEGORY_DELETED + " TEXT, " +
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

    public void addChange(String _id_, String category_name, int category_color_Ref, String category_deleted, String condition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Create new_note_table after getting data from firebase in first time.
        SharedPreferences prefs = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
        boolean change = prefs.getBoolean("change", false);
        if (change) {
            cv.put(COLUMN_ID_, _id_);
            cv.put(COLUMN_CATEGORY_NAME, category_name);
            cv.put(COLUMN_CATEGORY_COLOR, String.valueOf(category_color_Ref));
            cv.put(COLUMN_CATEGORY_DELETED, category_deleted);
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
