package com.ecommerce.scheduletime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.Notification.MyBroadcastReceiver;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class CreateAlarmNotification extends IntentService {

    Context context;

    ArrayList<String> task_id;
    ArrayList<String> task_id_;
    ArrayList<String> task_date;
    ArrayList<String> task_title;
    ArrayList<String> task_description;
    ArrayList<String> task_priority;
    ArrayList<String> task_category;
    ArrayList<String> task_time;
    ArrayList<String> task_done;
    ArrayList<String> task_reminder;

    List<Calendar> todayListCalendar;
    List<String> todayListId_;
    List<String> todayList_Id_;
    List<String> todayListTitle;
    List<String> todayListDescription;
    List<String> todayListPriority;
    List<String> todayListReminder;

    public CreateAlarmNotification() {
        super("CreateAlarmNotification");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Get all times of today date and sort them from 00:00 -> 23:59.
        task_id = new ArrayList<>();
        task_id_ = new ArrayList<>();
        task_date = new ArrayList<>();
        task_title = new ArrayList<>();
        task_description = new ArrayList<>();
        task_priority = new ArrayList<>();
        task_category = new ArrayList<>();
        task_time = new ArrayList<>();
        task_done = new ArrayList<>();
        task_reminder = new ArrayList<>();

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor taskCursor = myDB.readAllData();
        if (taskCursor.getCount() == 0) {
            // Empty.
        } else {
            while (taskCursor.moveToNext()) {
                task_id.add(taskCursor.getString(0));
                task_id_.add(taskCursor.getString(1));
                task_date.add(taskCursor.getString(2));
                task_title.add(taskCursor.getString(3));
                task_description.add(taskCursor.getString(4));
                task_priority.add(taskCursor.getString(5));
                task_category.add(taskCursor.getString(6));
                task_time.add(taskCursor.getString(7));
                task_done.add(taskCursor.getString(8));
                task_reminder.add(taskCursor.getString(9));
            }
        }

        List<Calendar> calendarList = new ArrayList<>();
        List<String> id_List = new ArrayList<>();
        List<String> _id_List = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        List<String> priorityList = new ArrayList<>();
        List<String> reminderList = new ArrayList<>();

        for (int i = 0; i < task_id.size(); i++) {

            List<String> date_ = Arrays.asList(task_date.get(i).split("-"));
            List<String> time_ = Arrays.asList(task_time.get(i).split(":"));
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

            calendarList.add(startTime);
            id_List.add(task_id.get(i));
            _id_List.add(task_id_.get(i));
            titleList.add(task_title.get(i));
            descriptionList.add(task_description.get(i));
            priorityList.add(task_priority.get(i));
            reminderList.add(task_reminder.get(i));
        }



        // getDates of today from times and sort them in order

        // get today's date
        Calendar today = Calendar.getInstance();

        // create a new list to hold the dates of today
        todayListCalendar = new ArrayList<>();
        todayListId_ = new ArrayList<>();
        todayList_Id_ = new ArrayList<>();
        todayListTitle = new ArrayList<>();
        todayListDescription = new ArrayList<>();
        todayListPriority = new ArrayList<>();
        todayListReminder = new ArrayList<>();

        // loop through the calendarList and add the dates of today to todayList
        for (int i = 0; i < calendarList.size(); i++) {
            if (calendarList.get(i).get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && calendarList.get(i).get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && calendarList.get(i).get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                todayListCalendar.add(calendarList.get(i));
                todayListId_.add(id_List.get(i));
                todayList_Id_.add(_id_List.get(i));
                todayListTitle.add(titleList.get(i));
                todayListDescription.add(descriptionList.get(i));
                todayListPriority.add(priorityList.get(i));
                todayListReminder.add(reminderList.get(i));

            }
        }

/*
        // convert ids to integer
        List<Integer> todayListId_Integer = new ArrayList<>();
        for (int i = 0; i < todayListId_.size(); i++) {
            todayListId_Integer.add(Integer.parseInt(todayListId_.get(i)));
        }

        // Duplicate task when reminder is not 0 with a random id different from other id_List_Integer and add to title of task the time reminded.
        for (int i = 0; i < todayListId_.size(); i++) {

            if (!todayListReminder.get(i).equals("0")) {
                // create new id random different from id_List_Integer
                Random rand = new Random();

                int newId;
                do {
                    newId = rand.nextInt(100);
                } while (todayListId_Integer.contains(newId));

                todayListCalendar.add(startTimeReminder(todayListId_.get(i), todayListReminder.get(i)));
                todayListId_.add(String.valueOf(newId));
                todayList_Id_.add(todayList_Id_.get(i));
                todayListTitle.add(todayListTitle.get(i) + "( " + todayListReminder.get(i) + context.getResources().getString(R.string.min_left) + " )");
                todayListDescription.add(todayListDescription.get(i));
                todayListPriority.add(todayListPriority.get(i));
                todayListReminder.add(todayListReminder.get(i));
            }
        }*/

        if (todayListCalendar.size() != 0){

            // sort the todayList in ascending order
            sortTodayList();

            List<Calendar> beforeNowCalendar = new ArrayList<>();
            List<Calendar> afterNowCalendar = new ArrayList<>();
            List<String> beforeNowId_ = new ArrayList<>();
            List<String> afterNowId_ = new ArrayList<>();
            List<String> beforeNow_Id_ = new ArrayList<>();
            List<String> afterNow_Id_ = new ArrayList<>();
            List<String> beforeNowTitle = new ArrayList<>();
            List<String> afterNowTitle = new ArrayList<>();
            List<String> beforeNowDescription = new ArrayList<>();
            List<String> afterNowDescription = new ArrayList<>();
            List<String> beforeNowPriority = new ArrayList<>();
            List<String> afterNowPriority = new ArrayList<>();

            // Leave only the dates that come after the present time.
            // For example : now it's 8h00, get only dates that superior or equal at 8h00 and remove dates that inferior at 8h00.
            for (int i = 0; i < todayListCalendar.size(); i++) {
                Calendar cal = Calendar.getInstance();
                if (cal.compareTo(todayListCalendar.get(i)) > 0) {
                    // cal is after today
                    beforeNowCalendar.add(todayListCalendar.get(i));
                    beforeNowId_.add(todayListId_.get(i));
                    beforeNow_Id_.add(todayList_Id_.get(i));
                    beforeNowTitle.add(todayListTitle.get(i));
                    beforeNowDescription.add(todayListDescription.get(i));
                    beforeNowPriority.add(todayListPriority.get(i));

                } else if (cal.compareTo(todayListCalendar.get(i)) < 0) {
                    // cal is before today
                    afterNowCalendar.add(todayListCalendar.get(i));
                    afterNowId_.add(todayListId_.get(i));
                    afterNow_Id_.add(todayList_Id_.get(i));
                    afterNowTitle.add(todayListTitle.get(i));
                    afterNowDescription.add(todayListDescription.get(i));
                    afterNowPriority.add(todayListPriority.get(i));

                } else {
                    // cal and today are equal
                }
            }

            for (int i = 0; i < afterNowId_.size(); i++) {

                Intent intent1 = new Intent(context, MyBroadcastReceiver.class);
                intent1.putExtra("id_", afterNowId_.get(i));
                intent1.putExtra("title_", afterNowTitle.get(i));
                intent1.putExtra("color_", afterNowPriority.get(i));
                intent1.putExtra("description", afterNowDescription.get(i));

                /**
                 For Alarm notifications,
                 Samsung devices have a limit of 500 scheduled alarms
                 (no matter if I cancel the alarms or set the flag FLAG_CANCEL_CURRENT).
                 */

                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                // Set alarm.
                // set(type, milliseconds, intent1)
                try {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(afterNowId_.get(i)),
                            intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                    alarm.set(AlarmManager.RTC_WAKEUP, afterNowCalendar.get(i).getTimeInMillis(), alarmIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), 10000 , alarmIntent);

                // Cancel alarm
                //alarm.cancel(alarmIntent);

            }
            //addTasksRemember();

        }else {}

    }

    private void addTasksRemember() {
        // convert ids to integer
        List<Integer> todayListId_Integer = new ArrayList<>();
        for (int i = 0; i < todayListId_.size(); i++) {
            todayListId_Integer.add(Integer.parseInt(todayListId_.get(i)));
        }

        // Duplicate task when reminder is not 0 with a random id different from other id_List_Integer and add to title of task the time reminded.
        for (int i = 0; i < todayListId_.size(); i++) {

            if (!todayListReminder.get(i).equals("0")) {
                // create new id random different from id_List_Integer
                Random rand = new Random();

                int newId;
                do {
                    newId = rand.nextInt(100);
                } while (todayListId_Integer.contains(newId));

                todayListCalendar.add(startTimeReminder(todayListId_.get(i), todayListReminder.get(i)));
                todayListId_.add(String.valueOf(newId));
                todayList_Id_.add(todayList_Id_.get(i));
                todayListTitle.add(todayListTitle.get(i) + "( " + todayListReminder.get(i) + context.getResources().getString(R.string.min_left) + " )");
                todayListDescription.add(todayListDescription.get(i));
                todayListPriority.add(todayListPriority.get(i));
                todayListReminder.add(todayListReminder.get(i));
            }
        }
    }

    private Calendar startTimeReminder(String id, String reminder) {
        Calendar startTime = Calendar.getInstance();

        for (int i = 0; i < task_id.size(); i++) {
            if (task_id.get(i).equals(id)){
                List<String> date_ = Arrays.asList(task_date.get(i).split("-"));
                List<String> time_ = Arrays.asList(task_time.get(i).split(":"));
                final int year = Integer.parseInt(date_.get(0));
                final int month = Integer.parseInt(date_.get(1));
                final int day = Integer.parseInt(date_.get(2));
                final int hour = Integer.parseInt(time_.get(0));
                final int minute = Integer.parseInt(time_.get(1));

                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, month);
                startTime.set(Calendar.DAY_OF_MONTH, day);
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);

                startTime.add(Calendar.MINUTE, - Integer.parseInt(reminder));
            }
        }
        return startTime;
    }

    private void sortTodayList() {

        int n = todayListCalendar.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (todayListCalendar.get(i).compareTo(todayListCalendar.get(j)) > 0) {
                    Calendar todayCalendar = todayListCalendar.get(i);
                    String todayId_ = todayListId_.get(i);
                    String today_Id_ = todayList_Id_.get(i);
                    String todayTitle = todayListTitle.get(i);
                    String todayDescription = todayListDescription.get(i);
                    String todayPriority = todayListPriority.get(i);
                    String todayReminder = todayListReminder.get(i);

                    todayListCalendar.set(i, todayListCalendar.get(j));
                    todayListId_.set(i, todayListId_.get(j));
                    todayList_Id_.set(i, todayList_Id_.get(j));
                    todayListTitle.set(i, todayListTitle.get(j));
                    todayListDescription.set(i, todayListDescription.get(j));
                    todayListPriority.set(i, todayListPriority.get(j));
                    todayListReminder.set(i, todayListReminder.get(j));

                    todayListCalendar.set(j, todayCalendar);
                    todayListId_.set(j, todayId_);
                    todayList_Id_.set(j, today_Id_);
                    todayListTitle.set(j, todayTitle);
                    todayListDescription.set(j, todayDescription);
                    todayListPriority.set(j, todayPriority);
                    todayListReminder.set(j, todayReminder);
                }
            }
        }
    }
}
