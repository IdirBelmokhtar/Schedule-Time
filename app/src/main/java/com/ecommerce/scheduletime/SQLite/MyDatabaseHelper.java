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
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.Notification.MyBroadcastReceiver;
import com.ecommerce.scheduletime.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "ScheduleTime.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_Tasks";
    private static final String COLUMN_ID = "_id";
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

    public void addSchedule(String date, String title, String description, String priority, String category, String time, String done, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

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

            /** @param Add Alarm Notification */
            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
            Cursor cursor = myDB.readAllData();
            int id_ = 0;
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    id_ = Integer.parseInt(cursor.getString(0));
                }
            }
            TASK_NEW_ID = id_;
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("id_", String.valueOf(id_));
            intent.putExtra("title_", title);
            intent.putExtra("color_", priority);
            intent.putExtra("description", description);

            /**
            For Alarm notifications,
            Samsung devices have a limit of 500 scheduled alarms
            (no matter if I cancel the alarms or set the flag FLAG_CANCEL_CURRENT).
            */

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Create time.
            List<String> date_ = Arrays.asList(date.split("-"));
            List<String> time_ = Arrays.asList(time.split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));


            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, minute);
            startTime.set(Calendar.SECOND, 0);

            // Set alarm.
            // set(type, milliseconds, intent)
            try {
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id_,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                //alarm.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), alarmIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), 10000 , alarmIntent);

            // Cancel alarm
            //alarm.cancel(alarmIntent);

            getData.set(year, month, day, hour, minute, 0); // Month start with 0
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

    public void updateData(String row_id, String date, String title, String description, String priority, String category, String time, String done, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

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
            /** @param Update Alarm Notification */
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("id_", row_id);
            intent.putExtra("title_", title);
            intent.putExtra("color_", priority);
            intent.putExtra("description", description);

            // Create time.
            List<String> date_ = Arrays.asList(date.split("-"));
            List<String> time_ = Arrays.asList(time.split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, minute);
            startTime.set(Calendar.SECOND, 0);

            try {
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(row_id),
                        intent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarm.cancel(alarmIntent);


                Calendar nowTime = Calendar.getInstance();
                if (nowTime.getTimeInMillis() < startTime.getTimeInMillis()) {
                    //alarm.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), alarmIntent);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
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
            /** @param Delete Alarm Notification */
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("id_", row_id);

            try {
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(row_id),
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
