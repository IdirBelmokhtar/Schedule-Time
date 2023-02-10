package com.ecommerce.scheduletime;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    public static long aLong = 1433701251000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
/*

        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        createEvents(compactCalendarView, MainActivity2.this);

        // Query for events on Sun, 07 Jun 2015 GMT.
        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitrary DateTime and you will receive all events for that day.
        Date date = new Date();
        date.setTime(Calendar.getInstance().getTimeInMillis());
        List<Event> events = compactCalendarView.getEvents(date); // can also take a Date object

        // events has size 2 with the 2 events inserted previously
        //Toast.makeText(this, "Events: " + events, Toast.LENGTH_SHORT).show();
        */
/*TextView textView = findViewById(R.id.longTime);
        textView.setText("Events: " + events);*//*


        compactCalendarView.setUseThreeLetterAbbreviation(true);

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                aLong = Calendar.getInstance().getTimeInMillis();
                Toast.makeText(MainActivity2.this, String.valueOf(dateClicked), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                //Toast.makeText(MainActivity2.this, "onMonthScroll", Toast.LENGTH_SHORT).show();
            }
        });
*/

    }

    private void createEvents(CompactCalendarView compactCalendarView, Context context){
        // Set Tasks SQLite into Realtime Database.
        ArrayList<String> task_id = new ArrayList<>();
        ArrayList<String> task_date = new ArrayList<>();
        ArrayList<String> task_title = new ArrayList<>();
        ArrayList<String> task_description = new ArrayList<>();
        ArrayList<String> task_priority = new ArrayList<>();
        ArrayList<String> task_category = new ArrayList<>();
        ArrayList<String> task_time = new ArrayList<>();

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor taskCursor = myDB.readAllData();
        if (taskCursor.getCount() == 0) {
            // Empty.
        } else {
            while (taskCursor.moveToNext()) {
                task_id.add(taskCursor.getString(0));
                task_date.add(taskCursor.getString(1));
                task_title.add(taskCursor.getString(2));
                task_description.add(taskCursor.getString(3));
                task_priority.add(taskCursor.getString(4));
                task_category.add(taskCursor.getString(5));
                task_time.add(taskCursor.getString(6));
            }
        }

        for (int i = 0; i < task_id.size(); i++) {

            // Create time.
            List<String> date_ = Arrays.asList(task_date.get(i).split("-"));
            List<String> time_ = Arrays.asList(task_time.get(i).split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));


            Calendar dateTime = Calendar.getInstance();
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, day);
            dateTime.set(Calendar.HOUR_OF_DAY, hour);
            dateTime.set(Calendar.MINUTE, minute);
            dateTime.set(Calendar.SECOND, 0);

            String color_ = task_priority.get(i);
            int color = ContextCompat.getColor(context, R.color.default_);

            if (color_.equals("default_") || color_.equals("default")){
                color = ContextCompat.getColor(context, R.color.default_);
            }else if (color_.equals("high")){
                color = ContextCompat.getColor(context, R.color.high);
            }else if (color_.equals("medium")){
                color = ContextCompat.getColor(context, R.color.medium);
            }else if (color_.equals("low")){
                color = ContextCompat.getColor(context, R.color.low);
            }

            Event ev = new Event(color, dateTime.getTimeInMillis(), task_title.get(i));
            compactCalendarView.addEvent(ev);

        }
    }

    private long convertTimeToMindNight(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}