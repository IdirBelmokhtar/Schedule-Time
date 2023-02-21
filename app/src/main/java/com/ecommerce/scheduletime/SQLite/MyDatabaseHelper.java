package com.ecommerce.scheduletime.SQLite;

import static android.content.Context.MODE_PRIVATE;

import static com.ecommerce.scheduletime.HomeActivity.MainActivity.getData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.CreateAlarmNotification;
import com.ecommerce.scheduletime.Notification.MyBroadcastReceiver;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.Sync.SyncDataBaseServiceUpdate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "ScheduleTime.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_Tasks";
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

    public static int TASK_NEW_ID = 0;

    public MyDatabaseHelper(@Nullable Context context) {
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
                COLUMN_REMINDER + " INTEGER);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String getPrimaryKey() { // ------------------------------try when category is empty after getting data from firebase
        String lastPrimaryKey = null;
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor categoryCursor = myDB.readAllData();
        if (categoryCursor.getCount() == 0) {
            lastPrimaryKey = "0";
        } else {
            while (categoryCursor.moveToNext()) {
                lastPrimaryKey = categoryCursor.getString(0);
            }
        }
        return lastPrimaryKey;
    }

    public void addSchedule1(String date, String title, String description, String priority, String category, String time, String done, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, String.valueOf(Integer.parseInt(getPrimaryKey()) + 1)); // + 1 to get the new id
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_PRIORITY, priority);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_DONE, done);
        cv.put(COLUMN_REMINDER, reminder);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = context.getSharedPreferences("swipe_hand", MODE_PRIVATE);
            int swipe = prefs.getInt("swipe", 0); //0 is the default value.
            if (swipe == 0){
                SharedPreferences.Editor editor = context.getSharedPreferences("swipe_hand", MODE_PRIVATE).edit();
                editor.putInt("swipe", 1);
                editor.apply();
            }
            /** @param Add_insert to {@link NewTasks} */
            SharedPreferences prefs_ = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
            boolean change = prefs_.getBoolean("change", false);
            if (change) {
                NewTasks newTasks = new NewTasks(context);
                newTasks.addChange(String.valueOf(Integer.parseInt(getPrimaryKey())), date, title, description, priority, category, time, done, String.valueOf(reminder), "insert");
            }

            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
            Cursor cursor = myDB.readAllData();
            int id_ = 0;
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    id_ = Integer.parseInt(cursor.getString(0));
                }
            }
            TASK_NEW_ID = id_;

            // Create time.
            List<String> date_ = Arrays.asList(date.split("-"));
            List<String> time_ = Arrays.asList(time.split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));

            getData.set(year, month, day, hour, minute, 0); // Month start with 0

        }

    }

    // We use this method when retrieve data from firebase with the original id and store them on our database
    public void addSchedule2(String _id_, String date, String title, String description, String priority, String category, String time, String done, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, _id_);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_PRIORITY, priority);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_DONE, done);
        cv.put(COLUMN_REMINDER, reminder);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add), Toast.LENGTH_SHORT).show();
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

    public void updateData(String row_id, String _id_, String date, String title, String description, String priority, String category, String time, String done, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_, _id_);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_PRIORITY, priority);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_DONE, done);
        cv.put(COLUMN_REMINDER, reminder);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_add), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
            /** @param Add_update to {@link NewTasks} */
                NewTasks newTasks = new NewTasks(context);
                newTasks.addChange(_id_, date, title, description, priority, category, time, done, String.valueOf(reminder), "update");

            /** @param call {@link SyncDataBaseServiceUpdate} when task has updated -- */
            Intent intent1 = new Intent(context, SyncDataBaseServiceUpdate.class);
            context.startService(intent1);

            /** @param call {@link CreateAlarmNotification} when task has updated -- */
            Intent intent2 = new Intent(context, CreateAlarmNotification.class);
            context.startService(intent2);
        }
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get data before delete
        String _id__ = null, date_ = null, title_ = null, description_ = null, priority_ = null, category_ = null, time_ = null, done_ = null, reminder_ = null;
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor cursor = myDB.readData(row_id);
        while (cursor.moveToNext()){
            _id__ = cursor.getString(1);
            date_ = cursor.getString(2);
            title_ = cursor.getString(3);
            description_ = cursor.getString(4);
            priority_ = cursor.getString(5);
            category_ = cursor.getString(6);
            time_ = cursor.getString(7);
            done_ = cursor.getString(8);
            reminder_ = cursor.getString(9);
        }

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, context.getResources().getString(R.string.failed_delete), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();

            /** @param Add_delete to {@link NewTasks} */
            NewTasks newTasks = new NewTasks(context);
            newTasks.addChange(_id__, date_, title_, description_,priority_, category_, time_, done_, reminder_, "delete");

            /** @param call {@link SyncDataBaseServiceUpdate} when task has deleted -- */
            Intent intent1 = new Intent(context, SyncDataBaseServiceUpdate.class);
            context.startService(intent1);

            /** @param Delete Alarm Notification */
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("id_", row_id);

            try {
                assert _id__ != null;
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(_id__),
                        intent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarm.cancel(alarmIntent);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

}
