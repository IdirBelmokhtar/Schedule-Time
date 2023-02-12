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

import static java.sql.Types.NULL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ecommerce.scheduletime.Adapter.RecyclerViewSearchAdapter;
import com.ecommerce.scheduletime.Adapter.RecyclerViewTasksAdapter;
import com.ecommerce.scheduletime.Dialog.DialogNewTask;
import com.ecommerce.scheduletime.Dialog.DialogSearch;
import com.ecommerce.scheduletime.Dialog.DialogSort;
import com.ecommerce.scheduletime.Model.RecyclerViewSearch;
import com.ecommerce.scheduletime.Model.Tasks;
import com.ecommerce.scheduletime.NoteActivity.NoteActivity;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListFragment extends Fragment {

    MyDatabaseHelper myDB;
    ArrayList<String> task_id, task_date, task_title, task_description, task_priority, task_category, task_time, task_done, task_reminder;
    Cursor cursor, cursor_;

    List<String> desiredTaskIds = new ArrayList<>();
    List<Tasks> desiredTasks_ = new ArrayList<>();

    List<RecyclerViewSearch> recyclerViewSearchList = new ArrayList<>();
    List<RecyclerViewSearch> searchList = new ArrayList<>();
    RecyclerViewSearchAdapter recyclerViewSearchAdapter;

    private AppBarLayout appBarLayout;
    private TextView list_date_textView1, list_date_textView2;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private boolean expanded = false;
    private TextView toolbar_title, sortList;
    private ImageView returnToToday;
    public static LinearLayout calendarViewHorizontal_layout;
    public static Calendar startDate;
    public static Calendar endDate;
    public static HorizontalCalendar horizontalCalendar;
    private NestedScrollView nestedScroll_list;
    private TextView firstTask_title, secondTask_title;
    private LinearLayout list_sort_up;
    private CardView list_sort_up_;
    Tasks tasks_ = null;

    public static int sort = 0;
    private List<Tasks> tasks1, tasks2;
    private RecyclerView recyclerViewTask_1, recyclerViewTask_2;
    private RecyclerViewTasksAdapter recyclerViewTasksListAdapter_1, recyclerViewTasksListAdapter_2;

    private LinearLayout no_data_list;

    private LottieAnimationView swipe_hand, click_sort_by;

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
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Swipe hand help (1st time)
        SharedPreferences preferences1 = container.getContext().getSharedPreferences("swipe_hand", MODE_PRIVATE);
        int swipe = preferences1.getInt("swipe", 0); //0 is the default value.
        swipe_hand = view.findViewById(R.id.swipe_hand_list);
        click_sort_by = view.findViewById(R.id.click_sort_by);

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
        view.findViewById(R.id.status_bar_list).setLayoutParams(params);
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinatorLayout_list);
        coordinatorLayout.setPadding(0, result, 0, 0);

        /******/
        mCollapsingToolbar = view.findViewById(R.id.mCollapsingToolbar_list);
        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_list);
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
        toolbar_title = view.findViewById(R.id.toolbar_list_title);
        toolbar_title.setVisibility(View.GONE);

        list_date_textView1 = view.findViewById(R.id.list_date_textView1);
        list_date_textView2 = view.findViewById(R.id.list_date_textView2);

        list_sort_up = view.findViewById(R.id.list_sort_up);
        list_sort_up_ = view.findViewById(R.id.list_sort_up_);
        list_sort_up.setVisibility(View.GONE);
        list_sort_up_.setVisibility(View.GONE);

        nestedScroll_list = view.findViewById(R.id.nestedScroll_list);
        sortList = view.findViewById(R.id.sortList);
        firstTask_title = view.findViewById(R.id.firstTask_title);
        secondTask_title = view.findViewById(R.id.secondTask_title);

        calendarViewHorizontal_layout = view.findViewById(R.id.calendarViewHorizontal_layout);
        returnToToday = view.findViewById(R.id.returnToToday);

        //RecyclerView 2 declaration.
        recyclerViewTask_2 = view.findViewById(R.id.recyclerViewTasksList_2);
        recyclerViewTask_2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        date_selected = currentTime;
        if (scrollToNewTask) {
            dateOfTheDayDesiered_list(getData.getTime());
            if ((date_selected.getDay() != getData.getTime().getDay()) && (date_selected.getDay() != getData.getTime().getMonth()) && (date_selected.getDay() != getData.getTime().getDay())) {
                returnToToday.setVisibility(View.VISIBLE);
                firstTask_title.setText(getResources().getString(R.string.tasks));
                secondTask_title.setVisibility(View.GONE);
                recyclerViewTask_2.setVisibility(View.GONE);
            }
            date_selected = getData.getTime();

        } else {
            dateOfTheDayDesiered_list(date_selected);
            returnToToday.setVisibility(View.GONE);
            firstTask_title.setText(getResources().getString(R.string.today_tasks));
            secondTask_title.setVisibility(View.VISIBLE);
            recyclerViewTask_2.setVisibility(View.VISIBLE);
        }

        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout_list);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                List<String> color_gradually = Arrays.asList("FF", "F2", "E6", "D9", "CC", "BF", "B3", "A6", "99"
                        , "8C", "80", "73", "66", "59", "4D", "40", "33"
                        , "26", "1A", "0D", "00");
                String white = "FFFFFF";

                int height = bottomAppBar.getHeight();
                int height1 = fab.getHeight() / 2;

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

        //List calendar HorizontalScrolling
        /* starts before 1 month from now */
        startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 year from now */
        endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 1);

        int c1 = ContextCompat.getColor(container.getContext(), R.color._white);
        int c2 = ContextCompat.getColor(container.getContext(), R.color.white);
        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarViewHorizontal)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()

                .textSize(NULL, 18, 14)
                .colorTextMiddle(c1, c2)
                .colorTextBottom(c1, c2)
                .showTopText(false)
                .formatBottomText("EEE")

                .end()
                .defaultSelectedDate(getData)
                .build();

        updateVisibilityOfHorizontalCalendar();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                returnToToday.setVisibility(View.VISIBLE);
                firstTask_title.setText(getResources().getString(R.string.tasks));
                secondTask_title.setVisibility(View.GONE);
                recyclerViewTask_2.setVisibility(View.GONE);

                Date calendar = new Date();
                calendar.setTime(date.getTimeInMillis());

                date_selected = calendar;

                storeDataInArraysCalenderList(calendar, view);

                dateOfTheDayDesiered_list(calendar);

                storeDataOfTomorrowInRecyclerView2();
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {
                //Calendar Scrolling
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                Toast.makeText(container.getContext(), date.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Sort List
        //RecyclerView 1 declaration.
        recyclerViewTask_1 = view.findViewById(R.id.recyclerViewTasksList_1);
        recyclerViewTask_1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        no_data_list = view.findViewById(R.id.no_data_list);
        no_data_list.setVisibility(View.GONE);

        sortList.setText(getResources().getString(R.string.sort_by_time));
        sort = 0;
        sortList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSort dialogSort = new DialogSort((Activity) container.getContext());
                dialogSort.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSort.getExit().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogSort.dismiss();
                    }
                });
                dialogSort.getSort_radioGroup().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        //checkBtn(sort);

                        int id = dialogSort.getSort_radioGroup().getCheckedRadioButtonId();
                        switch (id) {
                            case R.id.sort_date:
                                sortList.setText(getResources().getString(R.string.sort_by_time));
                                sort = 0;
                                if (scrollToNewTask) {
                                    storeDataInArraysCalenderList(getData.getTime(), view);
                                } else {
                                    storeDataInArraysCalenderList(date_selected, view);
                                }
                                dialogSort.dismiss();
                                break;

                            case R.id.sort_priority:
                                sortList.setText(getResources().getString(R.string.sort_by_priority));
                                sort = 1;
                                if (scrollToNewTask) {
                                    storeDataInArraysCalenderList(getData.getTime(), view);
                                } else {
                                    storeDataInArraysCalenderList(date_selected, view);
                                }
                                dialogSort.dismiss();
                                break;

                            case R.id.sort_category:
                                sortList.setText(getResources().getString(R.string.sort_by_category));
                                sort = 2;
                                if (scrollToNewTask) {
                                    storeDataInArraysCalenderList(getData.getTime(), view);
                                } else {
                                    storeDataInArraysCalenderList(date_selected, view);
                                }
                                dialogSort.dismiss();
                                break;

                            case R.id.sort_task_done:
                                sortList.setText(getResources().getString(R.string.sort_by_tasks_done));
                                sort = 3;
                                if (scrollToNewTask) {
                                    storeDataInArraysCalenderList(getData.getTime(), view);
                                } else {
                                    storeDataInArraysCalenderList(date_selected, view);
                                }
                                dialogSort.dismiss();
                                SharedPreferences preferences2 = getContext().getSharedPreferences("click_hand", MODE_PRIVATE);
                                int click = preferences2.getInt("click", 0); //0 is the default value.

                                if (click == 2) {
                                    // Show guidance
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.swipe_right), Toast.LENGTH_LONG).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.swipe_right), Toast.LENGTH_LONG).show();
                                        }
                                    }, 3500);

                                    SharedPreferences.Editor editor_ = getContext().getSharedPreferences("click_hand", MODE_PRIVATE).edit();
                                    editor_.putInt("click", 3);
                                    editor_.apply();
                                } else {
                                    click_sort_by.setVisibility(View.GONE);
                                    dialogSort.getClick_task_done().setVisibility(View.GONE);
                                }

                                break;
                            default:
                                break;
                        }
                    }
                });

                // Click hand help (1st time)
                SharedPreferences preferences2 = getContext().getSharedPreferences("click_hand", MODE_PRIVATE);
                int click = preferences2.getInt("click", 0); //0 is the default value.

                if (click == 1 || click == 2) {
                    click_sort_by.setVisibility(View.GONE);
                    // show click hand in dialog
                    dialogSort.getClick_task_done().setVisibility(View.VISIBLE);

                    SharedPreferences.Editor editor_ = getContext().getSharedPreferences("click_hand", MODE_PRIVATE).edit();
                    editor_.putInt("click", 2);
                    editor_.apply();
                } else {
                    click_sort_by.setVisibility(View.GONE);
                    dialogSort.getClick_task_done().setVisibility(View.GONE);
                }

                dialogSort.build();
                dialogSort.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }
        }); // Sort List <<<<<<<<-----------------
        returnToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarViewHorizontal_layout.getVisibility() == View.GONE) {
                    calendarViewHorizontal_layout.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            horizontalCalendar.goToday(true);
                            returnToToday.setVisibility(View.GONE);
                            firstTask_title.setText(getResources().getString(R.string.today_tasks));
                            secondTask_title.setVisibility(View.VISIBLE);
                            recyclerViewTask_2.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                } else {
                    horizontalCalendar.goToday(true);
                    returnToToday.setVisibility(View.GONE);
                    firstTask_title.setText(getResources().getString(R.string.today_tasks));
                    secondTask_title.setVisibility(View.VISIBLE);
                    recyclerViewTask_2.setVisibility(View.VISIBLE);
                }
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
            storeDataInArraysCalenderList(getData.getTime(), view);
        } else {
            storeDataInArraysCalenderList(date_selected, view);
        }

        /** do this if date is current date, if not go to the date and scroll it */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollToNewTask) {
                    float y = recyclerViewTask_1.getY() + recyclerViewTask_1.getChildAt(idNewTaskPosition).getY();
                    nestedScroll_list.smoothScrollTo(0, (int) y / 2);
                    scrollToNewTask = false;
                    hideBottomAppBar();

                    appBarLayout.setExpanded(false);
                }
            }
        }, 200);

        no_data_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask(getContext());
            }
        });

        return view;
    }


    public void createNewTask(Context context) {
        currentTime = Calendar.getInstance().getTime();

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

    private void storeDataOfTomorrowInRecyclerView2() {
        DateFormat dateFormat_yyyy = new SimpleDateFormat("yyyy");
        DateFormat dateFormat_M = new SimpleDateFormat("M");
        DateFormat dateFormat_d = new SimpleDateFormat("d");

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        String desired_yyyy = String.valueOf(dateFormat_yyyy.format(tomorrow.getTime()));
        String desired_M = String.valueOf(dateFormat_M.format(tomorrow.getTime()));
        String desired_d = String.valueOf(dateFormat_d.format(tomorrow.getTime()));

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

        List<String> desiredTaskIds = new ArrayList<>();

        if (sort == 3) {
            for (int i = 0; i < task_date.size(); i++) {
                if ((task_date.get(i).equals(desired_yyyy + "-" + String.valueOf(Integer.parseInt(desired_M) - 1) + "-" + desired_d)) && task_done.get(i).equals("yes")) {
                    desiredTaskIds.add(task_id.get(i));
                }
            }
        } else {
            for (int i = 0; i < task_date.size(); i++) {
                if ((task_date.get(i).equals(desired_yyyy + "-" + String.valueOf(Integer.parseInt(desired_M) - 1) + "-" + desired_d)) && task_done.get(i).equals("no")) {
                    desiredTaskIds.add(task_id.get(i));
                }
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
            Cursor cursor_ = myDatabaseHelper.readData(desiredTaskIds.get(i));
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

        // Sort by date and time.
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

        //RecyclerView 2 Adapter

        List<Tasks> desiredTasks_ = new ArrayList<>();

        for (int i = 0; i < desiredTaskIds.size(); i++) {
            desiredTasks_.add(new Tasks(task_id.get(i), task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i), task_category.get(i), task_time.get(i), task_done.get(i), task_reminder.get(i)));
        }
        recyclerViewTasksListAdapter_2 = new RecyclerViewTasksAdapter(desiredTasks_, getContext());
        recyclerViewTask_2.setAdapter(recyclerViewTasksListAdapter_2);

        if (desiredTasks_.size() == 0) {
            secondTask_title.setText(String.valueOf(""));
        } else {
            secondTask_title.setText(String.valueOf(getContext().getResources().getString(R.string.tomorrow)));
        }
    }

    private void updateVisibilityOfHorizontalCalendar() {
        if (getData.getTimeInMillis() < startDate.getTimeInMillis() || endDate.getTimeInMillis() < getData.getTimeInMillis()) {
            calendarViewHorizontal_layout.setVisibility(View.GONE);
        } else {
            calendarViewHorizontal_layout.setVisibility(View.VISIBLE);
        }
    }

    private void dateOfTheDayDesiered_list(Date calendar) {

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
            list_date_textView1.setText(getResources().getString(R.string.today));
            returnToToday.setVisibility(View.GONE);
            firstTask_title.setText(getResources().getString(R.string.today_tasks));
            secondTask_title.setVisibility(View.VISIBLE);
            recyclerViewTask_2.setVisibility(View.VISIBLE);
        } else if (dayDate.equals(yesterdayDate)) {
            list_date_textView1.setText(getResources().getString(R.string.yesterday));
        } else if (dayDate.equals(tomorrowDate)) {
            list_date_textView1.setText(getResources().getString(R.string.tomorrow));
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
                                    list_date_textView1.setText(R.string.this_week);
                                } else {
                                    if (dd <= 14) {
                                        //next week
                                        list_date_textView1.setText(getResources().getString(R.string.after_a_week));
                                    } else {
                                        if (dd <= 21) {
                                            //after 2 week
                                            list_date_textView1.setText(getResources().getString(R.string.after_two_weeks));
                                        } else {
                                            if (dd <= 28) {
                                                //after 3 week
                                                list_date_textView1.setText(getResources().getString(R.string.after_three_weeks));
                                            } else {
                                                if (dd <= 30) {
                                                    //after 4 week
                                                    list_date_textView1.setText(getResources().getString(R.string.after_four_weeks));
                                                }
                                            }
                                        }
                                    }

                                }

                            } else if (dd <= -2) {
                                /** Past weeks*/

                                if (dd >= -7) {
                                    //this week
                                    list_date_textView1.setText(getResources().getString(R.string.past_) + String.valueOf(Math.abs(dd)) + getResources().getString(R.string._days));
                                } else {
                                    if (dd >= -14) {
                                        //last week
                                        list_date_textView1.setText(getResources().getString(R.string.past_week));
                                    } else {
                                        if (dd >= -21) {
                                            //before 2 week
                                            list_date_textView1.setText(getResources().getString(R.string.before_two_weeks));
                                        } else {
                                            if (dd >= -28) {
                                                //before 3 week
                                                list_date_textView1.setText(getResources().getString(R.string.before_three_weeks));
                                            } else {
                                                if (dd >= -30) {
                                                    //before 4 week
                                                    list_date_textView1.setText(getResources().getString(R.string.before_four_weeks));
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
                        list_date_textView1.setText(getResources().getString(R.string.next_month));
                    } else {
                        list_date_textView1.setText(getResources().getString(R.string.next_) + String.valueOf(Math.abs(MM)) + getResources().getString(R.string._months));
                    }
                } else {
                    //before month
                    if (MM == -1) {
                        list_date_textView1.setText(getResources().getString(R.string.last_month));
                    } else {
                        list_date_textView1.setText(getResources().getString(R.string.last_) + String.valueOf(Math.abs(MM)) + getResources().getString(R.string._months));
                    }
                }

            } else if (yy >= 1) {
                //next years
                if (yy == 1) {
                    list_date_textView1.setText(getResources().getString(R.string.next_year));
                } else {
                    list_date_textView1.setText(getResources().getString(R.string.next_) + String.valueOf(Math.abs(yy)) + getResources().getString(R.string._years));
                }
            } else {
                //before years
                if (yy == -1) {
                    list_date_textView1.setText(getResources().getString(R.string.last_year));
                } else {
                    list_date_textView1.setText(getResources().getString(R.string.last_) + String.valueOf(Math.abs(yy)) + getResources().getString(R.string._years));
                }
            }

        }

        if (Integer.parseInt(dateFormat_yyyy.format(calendar)) > Integer.parseInt(dateFormat_yyyy.format(currentTime.getTime()))) {
            list_date_textView2.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_yyyy.format(calendar))));
            toolbar_title.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_yyyy.format(calendar))));
        } else {
            list_date_textView2.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_day.format(calendar))));
            toolbar_title.setText(String.valueOf(dateFormat_month.format(calendar))
                    + " " + String.valueOf(dateFormat_dd.format(calendar)
                    + ", " + String.valueOf(dateFormat_day.format(calendar))));
        }

    }

    private void storeDataInArraysCalenderList(Date desired_date, View view) {
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
        // tasks of the day
        desiredTaskIds.clear();
        if (sort == 3) {
            for (int i = 0; i < task_date.size(); i++) {
                if ((task_date.get(i).equals(desired_yyyy + "-" + String.valueOf(Integer.parseInt(desired_M) - 1) + "-" + desired_d)) && task_done.get(i).equals("yes")) {
                    desiredTaskIds.add(task_id.get(i));
                }
            }
        } else {
            for (int i = 0; i < task_date.size(); i++) {
                if ((task_date.get(i).equals(desired_yyyy + "-" + String.valueOf(Integer.parseInt(desired_M) - 1) + "-" + desired_d)) && task_done.get(i).equals("no")) {
                    desiredTaskIds.add(task_id.get(i));
                }
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
            no_data_list.setVisibility(View.VISIBLE);
            sortTasks(view);
        } else {
            no_data_list.setVisibility(View.GONE);
            sortTasks(view);
        }

        storeDataOfTomorrowInRecyclerView2();
    }

    private void sortTasks(View view) {
        if (sort == 0) {
            // Sort by Date and Time
            sortByDateAndTime();
        } else if (sort == 1) {
            // Sort by Priority
            sortByDateAndTime();
            sortByPriority();
        } else if (sort == 2) {
            // Sort by Category
            sortByDateAndTime();
            sortByCategory();
        } else if (sort == 3) {
            // Sort by Tasks Done
            sortByDateAndTime();
            sortByTasksDone();
        }

        addDataToRecyclerViewTask1(view, desiredTaskIds);
    }

    private void sortByTasksDone() {
        List<String> tasksDone = new ArrayList<>();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            tasksDone.add(task_done.get(i));
        }

        List<String> id = new ArrayList<>();
        List<String> date = new ArrayList<>();
        List<String> title = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<String> priority = new ArrayList<>();
        List<String> category = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> done = new ArrayList<>();
        List<String> reminder = new ArrayList<>();

        int size = desiredTaskIds.size();
        desiredTaskIds.clear();
        for (int i = 0; i < size; i++) {
            if (task_done.get(i).equals("yes")) {
                desiredTaskIds.add(task_id.get(i));
                id.add(task_id.get(i));
                date.add(task_date.get(i));
                title.add(task_title.get(i));
                description.add(task_description.get(i));
                priority.add(task_priority.get(i));
                category.add(task_category.get(i));
                time.add(task_time.get(i));
                done.add(task_done.get(i));
                reminder.add(task_reminder.get(i));
            }
        }

        // Update list of Task here
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
            task_id.add(id.get(i));
            task_date.add(date.get(i));
            task_title.add(title.get(i));
            task_description.add(description.get(i));
            task_priority.add(priority.get(i));
            task_category.add(category.get(i));
            task_time.add(time.get(i));
            task_done.add(done.get(i));
            task_reminder.add(reminder.get(i));
        }

    }

    private void sortByCategory() {

        List<Integer> tasksCategorySize = new ArrayList<>();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            tasksCategorySize.add(task_category.get(i).length());
        }

        for (int i = 0; i < desiredTaskIds.size() - 1; i++) {
            int index = i;

            for (int j = i + 1; j < desiredTaskIds.size(); j++) {
                if (tasksCategorySize.get(j) > tasksCategorySize.get(index)) {
                    index = j;
                }
            }

            int max = tasksCategorySize.get(index);
            tasksCategorySize.set(index, tasksCategorySize.get(i));
            tasksCategorySize.set(i, max);

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

    private void sortByPriority() {
        List<String> tasksPriority = new ArrayList<>();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            tasksPriority.add(task_priority.get(i));
        }

        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        ArrayList<String> priority = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> done = new ArrayList<>();
        ArrayList<String> reminder = new ArrayList<>();

        for (int i = 0; i < desiredTaskIds.size(); i++) {
            if (task_priority.get(i).equals("high")) {
                id.add(task_id.get(i));
                date.add(task_date.get(i));
                title.add(task_title.get(i));
                description.add(task_description.get(i));
                priority.add(task_priority.get(i));
                category.add(task_category.get(i));
                time.add(task_time.get(i));
                done.add(task_done.get(i));
                reminder.add(task_reminder.get(i));
            }
        }
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            if (task_priority.get(i).equals("medium")) {
                id.add(task_id.get(i));
                date.add(task_date.get(i));
                title.add(task_title.get(i));
                description.add(task_description.get(i));
                priority.add(task_priority.get(i));
                category.add(task_category.get(i));
                time.add(task_time.get(i));
                done.add(task_done.get(i));
                reminder.add(task_reminder.get(i));
            }
        }
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            if (task_priority.get(i).equals("low")) {
                id.add(task_id.get(i));
                date.add(task_date.get(i));
                title.add(task_title.get(i));
                description.add(task_description.get(i));
                priority.add(task_priority.get(i));
                category.add(task_category.get(i));
                time.add(task_time.get(i));
                done.add(task_done.get(i));
                reminder.add(task_reminder.get(i));
            }
        }
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            if (task_priority.get(i).equals("default")) {
                id.add(task_id.get(i));
                date.add(task_date.get(i));
                title.add(task_title.get(i));
                description.add(task_description.get(i));
                priority.add(task_priority.get(i));
                category.add(task_category.get(i));
                time.add(task_time.get(i));
                done.add(task_done.get(i));
                reminder.add(task_reminder.get(i));
            }
        }

        // Update list of Task here
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            task_id.set(i, id.get(i));
            task_date.set(i, date.get(i));
            task_title.set(i, title.get(i));
            task_description.set(i, description.get(i));
            task_priority.set(i, priority.get(i));
            task_category.set(i, category.get(i));
            task_time.set(i, time.get(i));
            task_done.set(i, done.get(i));
            task_reminder.set(i, reminder.get(i));
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

    private void addDataToRecyclerViewTask1(View view, List<String> desiredTaskIds) {
        //RecyclerView
        desiredTasks_.clear();
        for (int i = 0; i < desiredTaskIds.size(); i++) {
            desiredTasks_.add(new Tasks(task_id.get(i), task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i), task_category.get(i), task_time.get(i), task_done.get(i), task_reminder.get(i)));
        }
        recyclerViewTasksListAdapter_1 = new RecyclerViewTasksAdapter(desiredTasks_, getContext());
        recyclerViewTask_1.setAdapter(recyclerViewTasksListAdapter_1);
        swiped(desiredTasks_, recyclerViewTask_1);
    }

    private void showBottomAppBar() {
        // Show the bottomAppBar

        bottomAppBar.clearAnimation();
        bottomAppBar.animate().translationY(0).setDuration(200);
        fab.clearAnimation();
        fab.animate().translationY(0).setDuration(200);
        fab.show();

        list_sort_up.setVisibility(View.GONE);
        list_sort_up_.setVisibility(View.GONE);

    }

    private void hideBottomAppBar() {
        list_sort_up.setVisibility(View.GONE);
        list_sort_up_.setVisibility(View.GONE);

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
                list_sort_up.setVisibility(View.VISIBLE);
                list_sort_up_.setVisibility(View.VISIBLE);

                list_sort_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nestedScroll_list.smoothScrollTo(0, sortList.getTop());
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
                return true;
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
                        recyclerViewTasksListAdapter_1.notifyItemRemoved(position);

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
                                //myDB.addSchedule(tasks_.getTask_date(), tasks_.getTask_title(), tasks_.getTask_description(), tasks_.getTask_priority(), tasks_.getTask_category(), tasks_.getTask_time(), tasks_.getTask_done(), Integer.parseInt(tasks_.getTask_reminder()));
                                recyclerViewTasksListAdapter_1.notifyItemInserted(position);
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
                        tasks.remove(position);
                        recyclerViewTasksListAdapter_1.notifyItemRemoved(position);
                        if (tasks_.getTask_done().equals("no")) {
                            myDB.updateData(tasks_.getTask_id(), tasks_.getTask_date(), tasks_.getTask_title(), tasks_.getTask_description(), tasks_.getTask_priority(), tasks_.getTask_category(), tasks_.getTask_time(), "yes", Integer.parseInt(tasks_.getTask_reminder()));
                            Toast.makeText(getContext(), "Task : ( " + tasks_.getTask_title() + " ) " + getResources().getString(R.string.added_to_tasks_done_with_successfully), Toast.LENGTH_LONG).show();
                        } else if (tasks_.getTask_done().equals("yes")) {
                            myDB.updateData(tasks_.getTask_id(), tasks_.getTask_date(), tasks_.getTask_title(), tasks_.getTask_description(), tasks_.getTask_priority(), tasks_.getTask_category(), tasks_.getTask_time(), "no", Integer.parseInt(tasks_.getTask_reminder()));
                            Toast.makeText(getContext(), getResources().getString(R.string.task) + " : ( " + tasks_.getTask_title() + " ) " + getResources().getString(R.string.remove_from_tasks_done), Toast.LENGTH_LONG).show();
                        }

                        // Click hand help (1st time)
                        SharedPreferences preferences2 = getContext().getSharedPreferences("click_hand", MODE_PRIVATE);
                        int click = preferences2.getInt("click", 0); //0 is the default value.

                        if (click == 0) {
                            click_sort_by.setVisibility(View.VISIBLE);

                            SharedPreferences.Editor editor = getContext().getSharedPreferences("click_hand", MODE_PRIVATE).edit();
                            editor.putInt("click", 1);
                            editor.apply();
                        } else {
                            click_sort_by.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView
                    recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
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

                if (sort == 3) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftLabel(getResources().getString(R.string.delete))
                            .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                            .addSwipeLeftActionIcon(R.drawable.icons8_trash_can)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete))
                            .addSwipeRightLabel(getResources().getString(R.string.not_done))
                            .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                            .addSwipeRightActionIcon(R.drawable.icons8_minus)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.minus))
                            .create()
                            .decorate();
                } else {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftLabel(getResources().getString(R.string.delete))
                            .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                            .addSwipeLeftActionIcon(R.drawable.icons8_trash_can)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete))
                            .addSwipeRightLabel(getResources().getString(R.string.done))
                            .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                            .addSwipeRightActionIcon(R.drawable.icons8_checked)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.done))
                            .create()
                            .decorate();
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).

                attachToRecyclerView(recyclerViewTask);

    }

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

    public static void tapTargetSequenceList(View view, Context context) {
        TapTargetView.showFor((Activity) context ,
                TapTarget.forView(view.findViewById(R.id.note), context.getResources().getString(R.string.note), context.getResources().getString(R.string.track_your_notes_and_reflect_on_your_day))
                        // All options below are optional
                        .outerCircleColor(R.color.blue)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.transparent)
                        .titleTextSize(24)
                        .titleTextColor(R.color.white_)
                        .descriptionTextSize(14)
                        .textColor(R.color.white)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .targetRadius(60)
                        .icon(context.getResources().getDrawable(R.drawable.ic_icons8_note_1)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        Toast.makeText(context, context.getResources().getString(R.string.enjoy), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            String value = data.getStringExtra("key");
            // Update the UI based on the value
            if (value.equals("8")) {
                //restart listFragment and scroll to new task
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new ListFragment())
                        .commit();
                scrollToNewTask = true;
            }
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
                        nestedScroll_list.smoothScrollTo(0, sortList.getTop());
                        showBottomAppBar();
                        appBarLayout.setExpanded(true);
                    } else {
                        getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}