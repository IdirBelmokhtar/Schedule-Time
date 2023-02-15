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

public class MyDatabaseHelper_category extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "TaskCategory.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "my_Tasks_Category";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ID_ = "_id_";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_COLOR = "category_color_Ref";
    private static final String COLUMN_CATEGORY_DELETED = "category_deleted";

    public MyDatabaseHelper_category(@Nullable Context context) {
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
                COLUMN_CATEGORY_DELETED + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String getPrimaryKey() { // ------------------------------try when category is empty after getting data from firebase
        String lastPrimaryKey = null;
        MyDatabaseHelper_category myDB_category = new MyDatabaseHelper_category(context);
        Cursor categoryCursor = myDB_category.readAllData();
        if (categoryCursor.getCount() == 0) {
            lastPrimaryKey = "0";
        } else {
            while (categoryCursor.moveToNext()) {
                lastPrimaryKey = categoryCursor.getString(0);
            }
        }
        return lastPrimaryKey;
    }

    // Other method to get the last primary key (Chat gpt*)
    public int getLastPrimaryKey(SQLiteDatabase db, String tableName, String primaryKeyColumn) {
        int lastPrimaryKey = 0;
        Cursor cursor = db.rawQuery("SELECT max(" + primaryKeyColumn + ") FROM " + tableName + ";", null);
        if (cursor.moveToFirst()) {
            lastPrimaryKey = cursor.getInt(0);
        }
        cursor.close();
        return lastPrimaryKey;
    }

    public void addCategory1(String category_name, int category_color, String category_deleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // We add column for id we put the original id (the first id when we create a category)
        // We add this because Tasks take ids of categories in "task_category" column (Foreign Key for example)
        cv.put(COLUMN_ID_, String.valueOf(Integer.parseInt(getPrimaryKey()) + 1)); // + 1 to get the new id
        cv.put(COLUMN_CATEGORY_NAME, category_name);
        cv.put(COLUMN_CATEGORY_COLOR, String.valueOf(category_color));
        cv.put(COLUMN_CATEGORY_DELETED, category_deleted);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add_category), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

            /** @param Add_insert to {@link NewCategory} */

            SharedPreferences prefs = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
            boolean change = prefs.getBoolean("change", false);
            if (change) {
                NewCategory newCategory = new NewCategory(context);
                newCategory.addChange(String.valueOf(Integer.parseInt(getPrimaryKey())), category_name, category_color, category_deleted, "insert");
            }
        }

    }

    // We use this method when retrieve data from firebase with the original id and store them on our database
    public void addCategory2(String _id_, String category_name, int category_color, String category_deleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, _id_);
        cv.put(COLUMN_CATEGORY_NAME, category_name);
        cv.put(COLUMN_CATEGORY_COLOR, String.valueOf(category_color));
        cv.put(COLUMN_CATEGORY_DELETED, String.valueOf(category_deleted));

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add_category), Toast.LENGTH_SHORT).show();
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

    public Cursor readData_id_(String row_id_) {
        SQLiteDatabase db = this.getReadableDatabase();
        //get columns of signal data
        //String[] columns = {COLUMN_TITLE,COLUMN_AUTHOR,COLUMN_PAGES};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.query(TABLE_NAME, null, COLUMN_ID_ + "=?", new String[]{row_id_}, null, null, null, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String _id_, String name, int color, String category_deleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, _id_);
        cv.put(COLUMN_CATEGORY_NAME, name);
        cv.put(COLUMN_CATEGORY_COLOR, color);
        cv.put(COLUMN_CATEGORY_DELETED, category_deleted);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed delete", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();

            /** @param add_update to {@link NewCategory} */

            NewCategory newCategory = new NewCategory(context);
            newCategory.addChange(_id_, name, color, category_deleted, "update");
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
