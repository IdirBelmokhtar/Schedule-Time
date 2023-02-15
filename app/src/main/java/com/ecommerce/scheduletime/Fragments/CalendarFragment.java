package com.ecommerce.scheduletime.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewTasksAdapter.idNewTaskPosition;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.bottomAppBar;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.currentTime;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.date_selected;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.fOpen;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.fab;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.getData;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.mDrawerLayout;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.scrollToNewTask;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.setLocale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ecommerce.scheduletime.Adapter.RecyclerViewSearchAdapter;
import com.ecommerce.scheduletime.Adapter.RecyclerViewTasksAdapter;
import com.ecommerce.scheduletime.Dialog.DialogNewTask;
import com.ecommerce.scheduletime.Dialog.DialogSearch;
import com.ecommerce.scheduletime.HomeActivity.EditTaskActivity;
import com.ecommerce.scheduletime.Model.RecyclerViewSearch;
import com.ecommerce.scheduletime.Model.Tasks;
import com.ecommerce.scheduletime.NoteActivity.NoteActivity;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CalendarFragment extends Fragment {

    MyDatabaseHelper myDB;
    ArrayList<String> task_id, task_date, task_title, task_description, task_priority, task_category, task_time, task_done, task_reminder;
    Cursor cursor, cursor_;

    List<String> desiredTaskIds = new ArrayList<>();
    List<Tasks> desiredTasks_ = new ArrayList<>();

    List<RecyclerViewSearch> recyclerViewSearchList = new ArrayList<>();
    List<RecyclerViewSearch> searchList = new ArrayList<>();
    RecyclerViewSearchAdapter recyclerViewSearchAdapter;

    private AppBarLayout appBarLayout;
    private TextView calendar_date_textView1, calendar_date_textView2;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private boolean expanded = false;
    private TextView toolbar_title;
    private ImageView returnToCalendar, goToCalendar;
    private int year_ = Calendar.getInstance().get(Calendar.YEAR);
    private int month_ = Calendar.getInstance().get(Calendar.MONTH);
    private int dayOfMonth_ = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    public static CompactCalendarView compactCalendarView;
    private NestedScrollView nestedScroll_calendar;
    private TextView taskCalendar;
    private LinearLayout calendar_sort_up;
    private CardView calendar_sort_up_;
    private boolean scroll = false;
    Tasks tasks_ = null;

    public static RecyclerViewTasksAdapter recyclerViewTasksCalendarAdapter;

    //private List<Tasks> tasks;
    private RecyclerView recyclerViewTask;
    private LinearLayout no_data_calendar;

    private LottieAnimationView swipe_hand;

    public static boolean refresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences pref = getContext().getSharedPreferences("language", MODE_PRIVATE);
        String lang = pref.getString("lang", "");

        if (!lang.equals("")) {
            if (lang.equals("en")) {
                setLocale((Activity) container.getContext(), "en");
            } else if (lang.equals("fr")) {
                setLocale((Activity) container.getContext(), "fr");
            } else if (lang.equals("ar")) {
                setLocale((Activity) container.getContext(), "ar");
            }
        } else {
            String lang_ = Locale.getDefault().getLanguage();
            setLocale((Activity) container.getContext(), lang_);
            if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")) {
                //Toast.makeText(this, getResources().getString(R.string.this_language_is_not_currently_available), Toast.LENGTH_LONG).show();
            }
        }
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        // Swipe hand help
        SharedPreferences preferences = container.getContext().getSharedPreferences("swipe_hand", MODE_PRIVATE);
        int swipe = preferences.getInt("swipe", 0); //0 is the default value.
        swipe_hand = view.findViewById(R.id.swipe_hand_calendar);

        if (swipe == 1) {
            swipe_hand.setVisibility(View.VISIBLE);
        }

        // Make Status Bar (Auto Size).
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, result);
        view.findViewById(R.id.status_bar_calendar).setLayoutParams(params);
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinatorLayout_calendar);
        coordinatorLayout.setPadding(0, result, 0, 0);


        mCollapsingToolbar = view.findViewById(R.id.mCollapsingToolbar_calendar);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_calendar);
        recyclerViewSearchAdapter = new RecyclerViewSearchAdapter(container.getContext(), recyclerViewSearchList);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        //searchList.clear();
                        DialogSearch dialogSearch = new DialogSearch((Activity) container.getContext());

                        dialogSearch.getSearchViewDialog().clearFocus();
                        dialogSearch.getSearchViewDialog().setIconified(false);
                        //dialogSearch.getSearchViewDialog().setQueryHint(Html.fromHtml("<font color = #73A0D4>" + getResources().getString(R.string.search_hint) + "</font>"));
                        dialogSearch.getSearchViewDialog().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                searchList.clear();


                                for (RecyclerViewSearch viewSearch : recyclerViewSearchList) {
                                    if (viewSearch.getTitle().toLowerCase().contains(s.toLowerCase())) {
                                        searchList.add(viewSearch);
                                    }
                                }

                                if (searchList.isEmpty()) {
                                    //No data found
                                    dialogSearch.getRecyclerViewSearch().setVisibility(View.GONE);
                                    dialogSearch.getNoData().setVisibility(View.VISIBLE);
                                } else {
                                    if (s.equals("")) {
                                        dialogSearch.getRecyclerViewSearch().setVisibility(View.GONE);
                                    } else {
                                        dialogSearch.getRecyclerViewSearch().setVisibility(View.VISIBLE);
                                        dialogSearch.getNoData().setVisibility(View.GONE);
                                        recyclerViewSearchAdapter.setSearchList(container.getContext(), searchList);
                                    }
                                }

                                return true;
                            }
                        });

                        //RecyclerViewSearchDialog
                        recyclerViewSearchList.clear();
                        dialogSearch.getRecyclerViewSearch().setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
                        Uri path2 = Uri.parse("android.resource://com.ecommerce.eshop/" + R.drawable.schedule_time_icon);

                        ArrayList<String> task_id = new ArrayList<>();
                        ArrayList<String> task_date = new ArrayList<>();
                        ArrayList<String> task_title = new ArrayList<>();
                        ArrayList<String> task_description = new ArrayList<>();
                        ArrayList<String> task_priority = new ArrayList<>();
                        ArrayList<String> task_category = new ArrayList<>();
                        ArrayList<String> task_time = new ArrayList<>();
                        ArrayList<String> task_done = new ArrayList<>();
                        ArrayList<String> task_reminder = new ArrayList<>();

                        // Get data from DB.
                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext());
                        Cursor cursor = myDatabaseHelper.readAllData();
                        if (cursor.getCount() == 0) {
                            //no_data.setVisibility(View.VISIBLE);
                        } else {
                            //no_data.setVisibility(View.GONE);

                            while (cursor.moveToNext()) {
                                task_id.add(cursor.getString(0));
                                task_date.add(cursor.getString(1));
                                task_title.add(cursor.getString(2));
                                task_description.add(cursor.getString(3));
                                task_priority.add(cursor.getString(4));
                                task_category.add(cursor.getString(5));
                                task_time.add(cursor.getString(6));
                                task_done.add(cursor.getString(7));
                                task_reminder.add(cursor.getString(8));
                            }
                        }
                        for (int i = 0; i < task_id.size(); i++) {
                            recyclerViewSearchList.add(new RecyclerViewSearch(task_id.get(i), task_title.get(i), task_date.get(i), task_time.get(i)));
                        }

                        dialogSearch.getRecyclerViewSearch().setAdapter(recyclerViewSearchAdapter);
                        dialogSearch.getRecyclerViewSearch().setVisibility(View.GONE);

                        dialogSearch.build();
                        dialogSearch.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogSearch.getWindow().setWindowAnimations(R.style.SheetDialogAnimation);
                        dialogSearch.getWindow().setGravity(Gravity.BOTTOM);
                        return true;
                    case R.id.note:
                        // Start NoteActivity
                        Intent intent = new Intent(getContext(), NoteActivity.class);
                        startActivityForResult(intent, 7);
                        return true;
                }
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences prefs = getContext().getSharedPreferences("NOTE", MODE_PRIVATE);
        String note = prefs.getString("note", "");
        if (note.equals("")) {
            toolbar.getMenu().getItem(1).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icons8_note_2));
        } else {
            toolbar.getMenu().getItem(1).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_icons8_note_1));
        }
        toolbar_title = view.findViewById(R.id.toolbar_calendar_title);
        toolbar_title.setVisibility(View.GONE);

        calendar_date_textView1 = view.findViewById(R.id.calendar_date_textView1);
        calendar_date_textView2 = view.findViewById(R.id.calendar_date_textView2);

        calendar_sort_up = view.findViewById(R.id.calendar_sort_up);
        calendar_sort_up_ = view.findViewById(R.id.calendar_sort_up_);
        calendar_sort_up.setVisibility(View.GONE);
        calendar_sort_up_.setVisibility(View.GONE);

        nestedScroll_calendar = view.findViewById(R.id.nestedScroll_calendar);
        taskCalendar = view.findViewById(R.id.taskCalendar);

        compactCalendarView = view.findViewById(R.id.compactCalendarView);
        returnToCalendar = view.findViewById(R.id.returnToCalendar);
        goToCalendar = view.findViewById(R.id.goToCalendar);

        no_data_calendar = view.findViewById(R.id.no_data_calendar);
        no_data_calendar.setVisibility(View.GONE);

        DateFormat dateFormat_dd = new SimpleDateFormat("dd");
        DateFormat dateFormat_d = new SimpleDateFormat("d");
        DateFormat dateFormat_day = new SimpleDateFormat("EEEE");
        DateFormat dateFormat_MM = new SimpleDateFormat("MM");
        DateFormat dateFormat_M = new SimpleDateFormat("M");
        DateFormat dateFormat_month = new SimpleDateFormat("MMMM");
        DateFormat dateFormat_yyyy = new SimpleDateFormat("yyyy");

        date_selected = currentTime;
        if (scrollToNewTask) {
            dateOfTheDayDesiered_calender(getData.getTime());
            if ((date_selected.getDay() != getData.getTime().getDay()) && (date_selected.getDay() != getData.getTime().getMonth()) && (date_selected.getDay() != getData.getTime().getDay())) {
                returnToCalendar.setVisibility(View.VISIBLE);
            }
            date_selected = getData.getTime();

        } else {
            dateOfTheDayDesiered_calender(date_selected);
            returnToCalendar.setVisibility(View.GONE);
        }

        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout_calendar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                List<String> color_gradually = Arrays.asList("FF", "F2", "E6", "D9", "CC", "BF", "B3", "A6", "99"
                        , "8C", "80", "73", "66", "59", "4D", "40", "33"
                        , "26", "1A", "0D", "00");
                String white = "FFFFFF";

                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    expanded = true;
                    // Collapsed
                    toolbar_title.setVisibility(View.VISIBLE);
                    Collections.reverse(color_gradually);
                    delayText(0, color_gradually, toolbar_title, white);

                    //hide the bottomAppBar or BottomNavigationView
                    hideBottomAppBar();
                } else if (verticalOffset == 0) {
                    expanded = false;
                    // Expanded
                    toolbar_title.setVisibility(View.GONE);
                    delayText(0, color_gradually, toolbar_title, white);

                    //show the bottomAppBar or BottomNavigationView
                    showBottomAppBar();
                } else {
                    // Somewhere in between
                }
            }
        });

        //CalendarView
        /*calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + month + "/" + year;
                Toast.makeText(container.getContext(), date, Toast.LENGTH_SHORT).show();
            }
        });*/
        /**
         * We can use the hot plug feature, Quickly switch UI styles
         * calendarView.setWeekView(MeiZuWeekView.class);
         * calendarView.setMonthView(MeiZuMonthView.class);
         */
        //Toast.makeText(getContext(), getData.getTime().toString(), Toast.LENGTH_SHORT).show();

        recyclerViewTask = view.findViewById(R.id.recyclerViewTasksCalendar);

        compactCalendarView.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
        compactCalendarView.setCurrentDate(getData.getTime());

        compactCalendarView.setUseThreeLetterAbbreviation(true);
        createEvents(compactCalendarView, getContext());

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                returnToCalendar.setVisibility(View.VISIBLE);

                date_selected = dateClicked;

                storeDataInArraysCalender(dateClicked, view);

                dateOfTheDayDesiered_calender(dateClicked);

                if (!scroll) {
                    nestedScroll_calendar.smoothScrollTo(0, taskCalendar.getTop());
                    appBarLayout.setExpanded(false);
                    scroll = true;
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // --
                int year_month = firstDayOfNewMonth.getYear() + firstDayOfNewMonth.getMonth();
                int year_month_ = date_selected.getYear() + date_selected.getMonth();

                if (year_month != year_month_) {
                    returnToCalendar.setVisibility(View.VISIBLE);
                }
            }
        });

        returnToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendarView.setCurrentDate(currentTime);
                storeDataInArraysCalender(currentTime, view);
                dateOfTheDayDesiered_calender(currentTime);
                nestedScroll_calendar.smoothScrollTo(0, compactCalendarView.getTop());
                returnToCalendar.setVisibility(View.GONE);
                appBarLayout.setExpanded(true);
            }
        });

        goToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, dayOfMonth);
                                year_ = year;
                                month_ = month;
                                dayOfMonth_ = dayOfMonth;

                                date_selected = calendar.getTime();

                                compactCalendarView.setCurrentDate(date_selected);
                                storeDataInArraysCalender(date_selected, view);
                                dateOfTheDayDesiered_calender(date_selected);
                                nestedScroll_calendar.smoothScrollTo(0, compactCalendarView.getTop());
                                returnToCalendar.setVisibility(View.VISIBLE);
                                appBarLayout.setExpanded(true);
                            }
                        }, year_, month_, dayOfMonth_);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.updateDate(year_, month_, dayOfMonth_);
                datePickerDialog.show();

            }
        });

        myDB = new MyDatabaseHelper(getContext());
        task_id = new ArrayList<>();
        task_date = new ArrayList<>();
        task_title = new ArrayList<>();
        task_description = new ArrayList<>();
        task_priority = new ArrayList<>();
        task_category = new ArrayList<>();
        task_time = new ArrayList<>();
        task_done = new ArrayList<>();
        task_reminder = new ArrayList<>();

        if (scrollToNewTask) {
            storeDataInArraysCalender(getData.getTime(), view);
        } else {
            storeDataInArraysCalender(date_selected, view);
        }

        nestedScroll_calendar.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
                    /** "Scroll DOWN" */
                    //hideBottomAppBar();
                }
                if (scrollY < oldScrollY) {
                    /** "Scroll UP" */
                    //showBottomAppBar();
                }

                if (scrollY == 0) {
                    /** "TOP SCROLL" */
                }

                if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
                    /** "BOTTOM SCROLL" */
                }
            }
        });

        /** do this if date is current date, if not go to the date and scroll it */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollToNewTask) {
                    float y = recyclerViewTask.getY() + recyclerViewTask.getChildAt(idNewTaskPosition).getY();
                    nestedScroll_calendar.smoothScrollTo(0, (int) y);
                    scrollToNewTask = false;
                    hideBottomAppBar();
                    appBarLayout.setExpanded(false);
                }
            }
        }, 200);

        no_data_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask(getContext());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshFragment();
            }
        }, 5000);

        //Other things...
        //test(view);
        //test2(view);
        //test3(view);

        return view;
    }

    public static void createEvents(CompactCalendarView compactCalendarView, Context context) {
        compactCalendarView.removeAllEvents();

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

            List<String> date_ = Arrays.asList(task_date.get(i).split("-"));
            List<String> time_ = Arrays.asList(task_time.get(i).split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));

            java.util.Calendar dateTime = java.util.Calendar.getInstance();
            dateTime.set(java.util.Calendar.YEAR, year);
            dateTime.set(java.util.Calendar.MONTH, month);
            dateTime.set(java.util.Calendar.DAY_OF_MONTH, day);
            dateTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            dateTime.set(java.util.Calendar.MINUTE, minute);
            dateTime.set(java.util.Calendar.SECOND, 0);

            String color_ = task_priority.get(i);

            int color = ContextCompat.getColor(context, R.color.default_);

            if (color_.equals("default_") || color_.equals("default")) {
                color = ContextCompat.getColor(context, R.color.default_);
            } else if (color_.equals("high")) {
                color = ContextCompat.getColor(context, R.color.high);
            } else if (color_.equals("medium")) {
                color = ContextCompat.getColor(context, R.color.medium);
            } else if (color_.equals("low")) {
                color = ContextCompat.getColor(context, R.color.low);
            }

            Event ev = new Event(color, dateTime.getTimeInMillis(), task_title.get(i));
            compactCalendarView.addEvent(ev);
        }

        // Remove duplicates events
        for (int i = 0; i < task_id.size(); i++) {

            List<String> date_ = Arrays.asList(task_date.get(i).split("-"));
            List<String> time_ = Arrays.asList(task_time.get(i).split(":"));
            final int year = Integer.parseInt(date_.get(0));
            final int month = Integer.parseInt(date_.get(1));
            final int day = Integer.parseInt(date_.get(2));
            final int hour = Integer.parseInt(time_.get(0));
            final int minute = Integer.parseInt(time_.get(1));

            java.util.Calendar dateTime = java.util.Calendar.getInstance();
            dateTime.set(java.util.Calendar.YEAR, year);
            dateTime.set(java.util.Calendar.MONTH, month);
            dateTime.set(java.util.Calendar.DAY_OF_MONTH, day);
            dateTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            dateTime.set(java.util.Calendar.MINUTE, minute);
            dateTime.set(java.util.Calendar.SECOND, 0);

            List<Event> evs = compactCalendarView.getEvents(dateTime.getTimeInMillis());
            compactCalendarView.removeEvents(dateTime.getTimeInMillis());

            List<Integer> evs_color = new ArrayList<>();
            for (int j = 0; j < evs.size(); j++) {
                evs_color.add(evs.get(j).getColor());
            }

            Set<Integer> set = new HashSet<>(evs_color);
            evs_color.clear();
            evs_color.addAll(set);

            for (int j = 0; j < evs_color.size(); j++) {
                Event ev = new Event(evs_color.get(j), dateTime.getTimeInMillis(), task_title.get(i));
                compactCalendarView.addEvent(ev);
            }
        }
    }

    public void createNewTask(Context context) {
        currentTime = java.util.Calendar.getInstance().getTime();

        DialogNewTask dialogNewTask = new DialogNewTask((Activity) context);
        dialogNewTask.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogNewTask.getNew_saveBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                myDB.addSchedule(dialogNewTask.getDate(), dialogNewTask.getTitle(), dialogNewTask.getDescription(),
                        dialogNewTask.getPriority(), dialogNewTask.getCategory(), dialogNewTask.getTime(), "no",
                        dialogNewTask.getReminder());

                dialogNewTask.dismiss();

                if (fOpen == 1) {
                    //restart listFragment and scroll to new task
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new ListFragment())
                            .commit();
                    scrollToNewTask = true;
                } else if (fOpen == 2) {
                    //restart calendarFragment and scroll to new task
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new CalendarFragment())
                            .commit();
                    scrollToNewTask = true;
                }

            }
        });
        dialogNewTask.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //Toast.makeText(MainActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
            }
        });
        dialogNewTask.build();
        dialogNewTask.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void dateOfTheDayDesiered_calender(Date calendar) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat_dd = new SimpleDateFormat("dd");
        DateFormat dateFormat_day = new SimpleDateFormat("EEEE");
        DateFormat dateFormat_MM = new SimpleDateFormat("MM");
        DateFormat dateFormat_month = new SimpleDateFormat("MMMM");
        DateFormat dateFormat_yyyy = new SimpleDateFormat("yyyy");

        SharedPreferences pref = getContext().getSharedPreferences("language", MODE_PRIVATE);
        String lang = pref.getString("lang", "");

        if (!lang.equals("")) {
            if (lang.equals("en")) {
                Locale en = new Locale("en");
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", en);
                dateFormat_dd = new SimpleDateFormat("dd", en);
                dateFormat_day = new SimpleDateFormat("EEEE", en);
                dateFormat_MM = new SimpleDateFormat("MM", en);
                dateFormat_month = new SimpleDateFormat("MMMM", en);
                dateFormat_yyyy = new SimpleDateFormat("yyyy", en);
            } else if (lang.equals("fr")) {
                Locale fr = new Locale("fr");
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", fr);
                dateFormat_dd = new SimpleDateFormat("dd", fr);
                dateFormat_day = new SimpleDateFormat("EEEE", fr);
                dateFormat_MM = new SimpleDateFormat("MM", fr);
                dateFormat_month = new SimpleDateFormat("MMMM", fr);
                dateFormat_yyyy = new SimpleDateFormat("yyyy", fr);
            } else if (lang.equals("ar")) {
                Locale ar = new Locale("ar");
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", ar);
                dateFormat_dd = new SimpleDateFormat("dd", ar);
                dateFormat_day = new SimpleDateFormat("EEEE", ar);
                dateFormat_MM = new SimpleDateFormat("MM", ar);
                dateFormat_month = new SimpleDateFormat("MMMM", ar);
                dateFormat_yyyy = new SimpleDateFormat("yyyy", ar);
            }
        } else {
            String lang_ = Locale.getDefault().getLanguage();
            if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")) {
                Toast.makeText(getContext(), getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable), Toast.LENGTH_LONG).show();
            } else {
                Locale lng = Locale.getDefault();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", lng);
                dateFormat_dd = new SimpleDateFormat("dd", lng);
                dateFormat_day = new SimpleDateFormat("EEEE", lng);
                dateFormat_MM = new SimpleDateFormat("MM", lng);
                dateFormat_month = new SimpleDateFormat("MMMM", lng);
                dateFormat_yyyy = new SimpleDateFormat("yyyy", lng);
            }
        }

        java.util.Calendar _cal = java.util.Calendar.getInstance();
        _cal.add(java.util.Calendar.DATE, -1);
        java.util.Calendar cal_ = java.util.Calendar.getInstance();
        cal_.add(java.util.Calendar.DATE, 1);

        String dayDate = dateFormat.format(calendar);
        String yesterdayDate = dateFormat.format(_cal.getTime());
        String tomorrowDate = dateFormat.format(cal_.getTime());

        if (dayDate.equals(dateFormat.format(currentTime.getTime()))) {
            calendar_date_textView1.setText(getResources().getString(R.string.today));
            returnToCalendar.setVisibility(View.GONE);
        } else if (dayDate.equals(yesterdayDate)) {
            calendar_date_textView1.setText(getResources().getString(R.string.yesterday));
        } else if (dayDate.equals(tomorrowDate)) {
            calendar_date_textView1.setText(getResources().getString(R.string.tomorrow));
        } else {
            String my_year = dateFormat_yyyy.format(currentTime.getTime());
            String year = dateFormat_yyyy.format(calendar);
            int yy = Integer.parseInt(year) - Integer.parseInt(my_year);

            String my_month = dateFormat_MM.format(currentTime.getTime());
            String month = dateFormat_MM.format(calendar);
            int MM = Integer.parseInt(month) - Integer.parseInt(my_month);

            String my_day = dateFormat_dd.format(currentTime.getTime());
            String day = dateFormat_dd.format(calendar);
            int dd = Integer.parseInt(day) - Integer.parseInt(my_day);

            if (yy == 0) {
                //this year
                if (MM == 0) {
                    //this month
                    if (dd == 0) {//today
                        /**
                         * @param We done it before
                         */
                    } else {
                        if (dd == 1) {//tomorrow
                            /**
                             * @param We done it before
                             */} else if (dd == -1) {//yesterday
                            /**
                             * @param We done it before
                             */} else {
                            if (dd >= 2) {
                                /** Next weeks*/

                                if (dd <= 7) {
                                    //this week
                                    calendar_date_textView1.setText(getResources().getString(R.string.this_week));
                                } else {
                                    if (dd <= 14) {
                                        //next week
                                        calendar_date_textView1.setText(getResources().getString(R.string.after_a_week));
                                    } else {
                                        if (dd <= 21) {
                                            //after 2 week
                                            calendar_date_textView1.setText(getResources().getString(R.string.after_two_weeks));
                                        } else {
                                            if (dd <= 28) {
                                                //after 3 week
                                                calendar_date_textView1.setText(getResources().getString(R.string.after_three_weeks));
                                            } else {
                                                if (dd <= 30) {
                                                    //after 4 week
                                                    calendar_date_textView1.setText(getResources().getString(R.string.after_four_weeks));
                                                }
                                            }
                                        }
                                    }

                                }

                            } else if (dd <= -2) {
                                /** Past weeks*/

                                if (dd >= -7) {
                                    //this week
                                    calendar_date_textView1.setText(getResources().getString(R.string.past_) + String.valueOf(Math.abs(dd)) + getResources().getString(R.string._days));
                                } else {
                                    if (dd >= -14) {
                                        //last week
                                        calendar_date_textView1.setText(getResources().getString(R.string.past_week));
                                    } else {
                                        if (dd >= -21) {
                                            //before 2 week
                                            calendar_date_textView1.setText(getResources().getString(R.string.before_two_weeks));
                                        } else {
                                            if (dd >= -28) {
                                                //before 3 week
                                                calendar_date_textView1.setText(getResources().getString(R.string.before_three_weeks));
                                            } else {
                                                if (dd >= -30) {
                                                    //before 4 week
                                                    calendar_date_textView1.setText(getResources().getString(R.string.before_four_weeks));
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                } else if (MM >= 1) {
                    //next month
                    if (MM == 1) {
                        calendar_date_textView1.setText(getResources().getString(R.string.next_month));
                    } else {
                        calendar_date_textView1.setText(getResources().getString(R.string.next_) + String.valueOf(Math.abs(MM)) + getResources().getString(R.string._months));
                    }
                } else {
                    //before month
                    if (MM == -1) {
                        calendar_date_textView1.setText(getResources().getString(R.string.last_month));
                    } else {
                        calendar_date_textView1.setText(getResources().getString(R.string.last_) + String.valueOf(Math.abs(MM)) + getResources().getString(R.string._months));
                    }
                }

            } else if (yy >= 1) {
                //next years
                if (yy == 1) {
                    calendar_date_textView1.setText(getResources().getString(R.string.next_year));
                } else {
                    calendar_date_textView1.setText(getResources().getString(R.string.next_) + String.valueOf(Math.abs(yy)) + getResources().getString(R.string._years));
                }
            } else {
                //before years
                if (yy == -1) {
                    calendar_date_textView1.setText(getResources().getString(R.string.last_year));
                } else {
                    calendar_date_textView1.setText(getResources().getString(R.string.last_) + String.valueOf(Math.abs(yy)) + getResources().getString(R.string._years));
                }
            }

        }

        if (Integer.parseInt(dateFormat_yyyy.format(calendar)) > Integer.parseInt(dateFormat_yyyy.format(currentTime.getTime()))) {
            calendar_date_textView2.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_yyyy.format(calendar))));
            toolbar_title.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_yyyy.format(calendar))));
        } else {
            calendar_date_textView2.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_day.format(calendar))));
            toolbar_title.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_day.format(calendar))));
        }

    }

    private void storeDataInArraysCalender(Date desired_date, View view) {
        Locale lang_ = new Locale("en");
        DateFormat dateFormat_yyyy = new SimpleDateFormat("yyyy", lang_);
        DateFormat dateFormat_M = new SimpleDateFormat("M", lang_);
        DateFormat dateFormat_d = new SimpleDateFormat("d", lang_);
        String desired_yyyy = String.valueOf(dateFormat_yyyy.format(desired_date.getTime()));
        String desired_M = String.valueOf(dateFormat_M.format(desired_date.getTime()));
        String desired_d = String.valueOf(dateFormat_d.format(desired_date.getTime()));

        task_id.clear();
        task_date.clear();
        task_title.clear();
        task_description.clear();
        task_priority.clear();
        task_category.clear();
        task_time.clear();
        task_done.clear();
        task_reminder.clear();

        cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            //data_no.setVisibility(View.VISIBLE);
            //no_data.setVisibility(View.VISIBLE);
        } else {
            //data_no.setVisibility(View.GONE);
            //no_data.setVisibility(View.GONE);

            while (cursor.moveToNext()) {
                task_id.add(cursor.getString(0));
                task_date.add(cursor.getString(1));
                task_title.add(cursor.getString(2));
                task_description.add(cursor.getString(3));
                task_priority.add(cursor.getString(4));
                task_category.add(cursor.getString(5));
                task_time.add(cursor.getString(6));
                task_done.add(cursor.getString(7));
                task_reminder.add(cursor.getString(8));
            }
        }

        desiredTaskIds.clear();
        for (int i = 0; i < task_date.size(); i++) {
            if (task_date.get(i).equals(desired_yyyy + "-" + String.valueOf(Integer.parseInt(desired_M) - 1) + "-" + desired_d)) {
                desiredTaskIds.add(task_id.get(i));
            }
        }
        task_id.clear();
        task_date.clear();
        task_title.clear();
        task_description.clear();
        task_priority.clear();
        task_category.clear();
        task_time.clear();
        task_done.clear();
        task_reminder.clear();

        for (int i = 0; i < desiredTaskIds.size(); i++) {
            cursor_ = myDB.readData(desiredTaskIds.get(i));
            while (cursor_.moveToNext()) {
                task_id.add(cursor_.getString(0));
                task_date.add(cursor_.getString(1));
                task_title.add(cursor_.getString(2));
                task_description.add(cursor_.getString(3));
                task_priority.add(cursor_.getString(4));
                task_category.add(cursor_.getString(5));
                task_time.add(cursor_.getString(6));
                task_done.add(cursor_.getString(7));
                task_reminder.add(cursor_.getString(8));
            }
        }


        if (desiredTaskIds.size() == 0) {
            no_data_calendar.setVisibility(View.VISIBLE);
            sortByDateAndTime();
            addDataToRecyclerViewTask(view, desiredTaskIds);
        } else {
            no_data_calendar.setVisibility(View.GONE);
            sortByDateAndTime();
            addDataToRecyclerViewTask(view, desiredTaskIds);
        }
    }

    private void sortByDateAndTime() {

        List<Integer> tasksTime = new ArrayList<>();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            tasksTime.add((Integer.parseInt(task_time.get(i).substring(0, 2)) * 60) + Integer.parseInt(task_time.get(i).substring(3, 5)));
        }

        for (int i = 0; i < desiredTaskIds.size() - 1; i++) {
            int index = i;

            for (int j = i + 1; j < desiredTaskIds.size(); j++) {
                if (tasksTime.get(j) < tasksTime.get(index)) {
                    index = j;
                }
            }

            int min = tasksTime.get(index);
            tasksTime.set(index, tasksTime.get(i));
            tasksTime.set(i, min);

            String id = task_id.get(index);
            String date = task_date.get(index);
            String title = task_title.get(index);
            String description = task_description.get(index);
            String priority = task_priority.get(index);
            String category = task_category.get(index);
            String time = task_time.get(index);
            String done = task_done.get(index);
            String reminder = task_reminder.get(index);

            task_id.set(index, task_id.get(i));
            task_date.set(index, task_date.get(i));
            task_title.set(index, task_title.get(i));
            task_description.set(index, task_description.get(i));
            task_priority.set(index, task_priority.get(i));
            task_category.set(index, task_category.get(i));
            task_time.set(index, task_time.get(i));
            task_done.set(index, task_done.get(i));
            task_reminder.set(index, task_reminder.get(i));

            task_id.set(i, id);
            task_date.set(i, date);
            task_title.set(i, title);
            task_description.set(i, description);
            task_priority.set(i, priority);
            task_category.set(i, category);
            task_time.set(i, time);
            task_done.set(i, done);
            task_reminder.set(i, reminder);

        }

    }

    private void addDataToRecyclerViewTask(View view, List<String> desiredTaskIds) {
        //RecyclerView
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        desiredTasks_.clear();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            desiredTasks_.add(new Tasks(task_id.get(i), task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i), task_category.get(i), task_time.get(i), task_done.get(i), task_reminder.get(i)));
        }
        recyclerViewTasksCalendarAdapter = new RecyclerViewTasksAdapter(desiredTasks_, getContext());
        recyclerViewTask.setAdapter(recyclerViewTasksCalendarAdapter);
        swiped(desiredTasks_, recyclerViewTask);
    }

    private void showBottomAppBar() {
        // Show the bottomAppBar

        bottomAppBar.clearAnimation();
        bottomAppBar.animate().translationY(0).setDuration(200);
        fab.clearAnimation();
        fab.animate().translationY(0).setDuration(200);
        fab.show();

        calendar_sort_up.setVisibility(View.GONE);
        calendar_sort_up_.setVisibility(View.GONE);

    }

    private void hideBottomAppBar() {
        calendar_sort_up.setVisibility(View.GONE);
        calendar_sort_up_.setVisibility(View.GONE);

        //hide the bottomAppBar
        int height = bottomAppBar.getHeight();
        int height1 = fab.getHeight() / 2;

        bottomAppBar.clearAnimation();
        bottomAppBar.animate().translationY(height).setDuration(200);
        fab.clearAnimation();
        fab.animate().translationY(height1).setDuration(200);
        fab.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calendar_sort_up.setVisibility(View.VISIBLE);
                calendar_sort_up_.setVisibility(View.VISIBLE);

                calendar_sort_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nestedScroll_calendar.smoothScrollTo(0, compactCalendarView.getTop());
                        showBottomAppBar();
                        appBarLayout.setExpanded(true);
                    }
                });
            }
        }, 500);

    }

    private void swiped(List<Tasks> tasks, RecyclerView recyclerViewTask) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        // Swipe hand help
                        SharedPreferences preferences = getContext().getSharedPreferences("swipe_hand", MODE_PRIVATE);
                        int swipe = preferences.getInt("swipe", 0); //0 is the default value.
                        if (swipe == 1) {
                            swipe_hand.setVisibility(View.GONE);

                            SharedPreferences.Editor editor = getContext().getSharedPreferences("swipe_hand", MODE_PRIVATE).edit();
                            editor.putInt("swipe", 2);
                            editor.apply();
                        }

                        tasks_ = tasks.get(position);
                        tasks.remove(position);
                        recyclerViewTasksCalendarAdapter.notifyItemRemoved(position);

                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                        title.setText(getResources().getString(R.string.delete_this_task) + " :\n\"" + tasks_.getTask_title() + "\" ?");
                        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tasks.add(position, tasks_);
                                recyclerViewTasksCalendarAdapter.notifyItemInserted(position);
                                dialog.dismiss();
                            }
                        });
                        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDB.deleteOneRow(tasks_.getTask_id());
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);

                        break;
                    case ItemTouchHelper.RIGHT:
                        tasks_ = tasks.get(position);
                        recyclerViewTasksCalendarAdapter.notifyDataSetChanged();
                        if (tasks_.getTask_done().equals("no")) {
                            myDB.updateData(tasks_.getTask_id(), tasks_.getTask_date(), tasks_.getTask_title(), tasks_.getTask_description(), tasks_.getTask_priority(), tasks_.getTask_category(), tasks_.getTask_time(), "yes", Integer.parseInt(tasks_.getTask_reminder()));
                            Toast.makeText(getContext(), getResources().getString(R.string.task) + " : ( " + tasks_.getTask_title() + " ) " + getResources().getString(R.string.added_to_tasks_done_with_successfully), Toast.LENGTH_LONG).show();
                        } else if (tasks_.getTask_done().equals("yes")) {
                            myDB.updateData(tasks_.getTask_id(), tasks_.getTask_date(), tasks_.getTask_title(), tasks_.getTask_description(), tasks_.getTask_priority(), tasks_.getTask_category(), tasks_.getTask_time(), "no", Integer.parseInt(tasks_.getTask_reminder()));
                            Toast.makeText(getContext(), getResources().getString(R.string.task) + " : ( " + tasks_.getTask_title() + " ) " + getResources().getString(R.string.remove_from_tasks_done), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                /*if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();

                    p.setColor(ContextCompat.getColor(getContext(), R.color.transparent));

                    if (dX > 0) {
                        //* Set your color for positive displacement

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                    } else {
                        //* Set your color for negative displacement

                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }

                }*/
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel(getResources().getString(R.string.delete))
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeLeftActionIcon(R.drawable.icons8_trash_can)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete))
                        .addSwipeRightLabel(getResources().getString(R.string.update))
                        .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeRightActionIcon(R.drawable.icons8_checkmark_minus)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.done_minus))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerViewTask);

    }

    /*
    private void test(View view) {
        TextView task = view.findViewById(R.id.taskCalendar);

        List<String> s = new ArrayList<>();

        s.clear();
        long i = 100000000L;

        while (i != 1000000000L) {
            if (i % 9 == 0) {
                if (Integer.parseInt(removeTheLastString(String.valueOf(i))) % 8 == 0) {

                    if (Integer.parseInt(removeTheLastString(removeTheLastString(String.valueOf(i)))) % 7 == 0) {

                        if (Integer.parseInt(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(String.valueOf(i)))))) % 5 == 0) {

                            if (Integer.parseInt(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(String.valueOf(i))))))) % 4 == 0) {

                                if (Integer.parseInt(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(String.valueOf(i)))))))) % 3 == 0) {

                                    if (Integer.parseInt(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(String.valueOf(i))))))))) % 2 == 0) {

                                        if (Integer.parseInt(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(removeTheLastString(String.valueOf(i)))))))))) % 1 == 0) {
                                            s.add(String.valueOf(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            i++;
        }

        task.setText(String.valueOf(s) + "\n" + String.valueOf(s.size()));
    }

    private void test2(View view) {
        TextView task = view.findViewById(R.id.taskCalendar);

        List<String> listPrisonniers = Arrays.asList(readRawTextFile(getContext(), R.raw.en).split("',\\s*'"));
        Collections.sort(listPrisonniers, String.CASE_INSENSITIVE_ORDER);

        List<String> bloc = new ArrayList<>();

        int x = 0;
        char alphabet = 'A';
        String prisonnier = "Prince Harry"; //7720
        List<Integer> fibonacci = new ArrayList<>();
        fibonacci = readFibonacci(20);//1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181...

        while (x != 20) {

            for (int i = 0; i < fibonacci.get(x); i++) {
                bloc.add(alphabet + String.valueOf(i + 1));
            }
            alphabet++;
            x++;
        }

        int y = 0;
        while (y != listPrisonniers.size()) {
            if (listPrisonniers.get(y).equals(prisonnier)) {
                task.setText("prisonnier N°" + String.valueOf(y) + "\n" +
                        "Nom : " + String.valueOf(listPrisonniers.get(y)) + "\n" +
                        "Bloc : " + String.valueOf(bloc.get(y)));
            }
            y++;
        }

    }

    public static List<Integer> readFibonacci(int number) {
        List<Integer> fib = new ArrayList<>();

        fib.add(1);
        fib.add(1);

        for (int i = 1; i <= number; i++) {
            fib.add(fib.get(i - 1) + fib.get(i));
        }

        return fib;
    }

    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }

    public static String removeTheLastString(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
*/
    private void delayText(int i, List<String> color, TextView textView, String colorCode) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i < color.size()) {
                    textView.setTextColor(Color.parseColor("#" + color.get(i) + colorCode));
                    delayText(i + 1, color, textView, colorCode);
                }
            }
        }, 8);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            String value = data.getStringExtra("key");
            // Update the UI based on the value
            if (value.equals("8")) {
                //restart calendarFragment and scroll to new task
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new CalendarFragment())
                        .commit();
                scrollToNewTask = true;
            }
        }
    }

    private void refreshFragment() {
        if (refresh) {
            refresh = false;
            try {
                Fragment fragment = new CalendarFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                scrollToNewTask = true;
            } catch (Exception e) {
                try {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.save_change), Toast.LENGTH_SHORT).show();
                }catch (Exception ignored) {
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshFragment();
                }
            }, 1000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    if (expanded) {
                        nestedScroll_calendar.smoothScrollTo(0, compactCalendarView.getTop());
                        appBarLayout.setExpanded(true);
                    } else {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new ListFragment())
                                .addToBackStack(null)
                                .commit();
                        fOpen = 1;
                    }
                    return true;
                }
                return false;
            }
        });
    }

}