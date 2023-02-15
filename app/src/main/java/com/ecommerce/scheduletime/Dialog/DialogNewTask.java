package com.ecommerce.scheduletime.Dialog;

import static android.content.Context.MODE_PRIVATE;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.currentTime;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.date_selected;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;
import com.ecommerce.scheduletime.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DialogNewTask extends Dialog {

    //field time
    Calendar calendar = Calendar.getInstance();
    Calendar calendar_time = Calendar.getInstance();
    int year_, month_, dayOfMonth_, hour_, minute_;

    Calendar calendar_start = Calendar.getInstance();
    int year_start, month_start, dayOfMonth_start;
    Calendar calendar_end = Calendar.getInstance();
    int year_end, month_end, dayOfMonth_end;

    //field DataBase
    MyDatabaseHelper_category myDB_category;
    ArrayList<String> category_id, category_id_, category_name, category_color, category_deleted;
    Button[] category_btn;

    //field
    private Button new_details, new_tags, new_deadline;
    private LinearLayout dialog_details, dialog_tags, dialog_deadline;
    private TextView new_cancelBtn1, new_cancelBtn2, new_cancelBtn3;
    private Button new_nextBtn1, new_nextBtn2;

    private EditText new_task_Title, new_task_Description;
    private Button new_priority_low, new_priority_medium, new_priority_high;
    private CircleImageView new_category_add;
    private LinearLayout categorys_layout, add_category_layout;
    private TextView textView_month, textView_day, textView_year, textView_hour, textView_minute, textView_time_mode;
    private TextView textViewDate, textViewSelectDays, textView_startDate, textView_endDate;
    private LinearLayout linearLayoutDate, linearLayoutSelectDays, linearLayoutSelectDate, textViewStartAndEndDate;
    private Button days_sunday, days_monday, days_tuesday, days_wednesday, days_thursday, days_friday, days_saturday;
    private Spinner spinner_reminder_time, spinner_repeat_time;
    private Button new_saveBtn;
    //category
    private EditText editText_category;
    private LinearLayout _editText_category, category_colors;
    private CardView editText_category_icon;
    private ImageView editText_category_icon_;
    private CircleImageView editText_category_done;
    private CircleImageView category_color1, category_color2, category_color3, category_color4, category_color5, category_color6, category_color7;

    int color_;

    //Data
    public String title, description, priority, time;
    List<String> category = new ArrayList<>();

    Calendar calNow, calChose;
    private TextView textView_time_left;

    int reminder = -1;
    int repeat = 0;
    boolean sunday = true, monday = false, tuesday = false, wednesday = false, thursday = false, friday = false, saturday = false;

    //constructor
    public DialogNewTask(Activity activity) {
        super(activity, R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.dialog_new_task);

        this.new_details = findViewById(R.id.new_details);
        this.new_tags = findViewById(R.id.new_tags);
        this.new_deadline = findViewById(R.id.new_deadline);

        this.dialog_details = findViewById(R.id.dialog_details);
        this.dialog_tags = findViewById(R.id.dialog_tags);
        this.dialog_deadline = findViewById(R.id.dialog_deadline);

        this.new_cancelBtn1 = findViewById(R.id.new_cancelBtn1);
        this.new_cancelBtn2 = findViewById(R.id.new_cancelBtn2);
        this.new_cancelBtn3 = findViewById(R.id.new_cancelBtn3);

        this.new_nextBtn1 = findViewById(R.id.new_nextBtn1);
        this.new_nextBtn2 = findViewById(R.id.new_nextBtn2);

        //re-installer color of dialog_ when start fab
        dialog_details.setVisibility(View.VISIBLE);
        changeColorBtn(new_details, R.color.blue_2);
        dialog_tags.setVisibility(View.GONE);
        changeColorBtn(new_tags, R.color.blue);
        dialog_deadline.setVisibility(View.GONE);
        changeColorBtn(new_deadline, R.color.blue);

        new_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if no view has focus:
                hideKeyboard(view);

                dialog_details.setVisibility(View.VISIBLE);
                changeColorBtn(new_details, R.color.blue_2);
                dialog_tags.setVisibility(View.GONE);
                changeColorBtn(new_tags, R.color.blue);
                dialog_deadline.setVisibility(View.GONE);
                changeColorBtn(new_deadline, R.color.blue);
            }
        });
        new_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if no view has focus:
                hideKeyboard(view);
                if (titleIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.VISIBLE);
                    changeColorBtn(new_tags, R.color.blue_2);
                    dialog_deadline.setVisibility(View.GONE);
                    changeColorBtn(new_deadline, R.color.blue);
                } else {
                    new_task_Title.setError(getContext().getResources().getString(R.string.title_is_empty));
                }
                if (descriptionIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.VISIBLE);
                    changeColorBtn(new_tags, R.color.blue_2);
                    dialog_deadline.setVisibility(View.GONE);
                    changeColorBtn(new_deadline, R.color.blue);
                } else {
                    new_task_Description.setError(getContext().getResources().getString(R.string.description_is_empty));
                }
            }
        });
        new_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update current Time.
                currentTime = Calendar.getInstance().getTime();
                hour_ = currentTime.getHours() + 1;
                minute_ = 0;
                calendar_time.set(0, 0, 0, hour_, minute_);

                // Set textViews of time from time of your phone.
                setHourAndMinuteInTextViewTime();

                // Check if no view has focus:
                hideKeyboard(view);
                if (titleIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.GONE);
                    changeColorBtn(new_tags, R.color.blue);
                    dialog_deadline.setVisibility(View.VISIBLE);
                    changeColorBtn(new_deadline, R.color.blue_2);
                } else {
                    new_task_Title.setError(getContext().getResources().getString(R.string.title_is_empty));
                }
                if (descriptionIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.GONE);
                    changeColorBtn(new_tags, R.color.blue);
                    dialog_deadline.setVisibility(View.VISIBLE);
                    changeColorBtn(new_deadline, R.color.blue_2);
                } else {
                    new_task_Description.setError(getContext().getResources().getString(R.string.description_is_empty));
                }
            }
        });

        new_cancelBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        new_cancelBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        new_cancelBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        new_nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if no view has focus:
                hideKeyboard(view);
                if (titleIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.VISIBLE);
                    changeColorBtn(new_tags, R.color.blue_2);
                    dialog_deadline.setVisibility(View.GONE);
                    changeColorBtn(new_deadline, R.color.blue);
                } else {
                    new_task_Title.setError(getContext().getResources().getString(R.string.title_is_empty));
                }
                if (descriptionIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.VISIBLE);
                    changeColorBtn(new_tags, R.color.blue_2);
                    dialog_deadline.setVisibility(View.GONE);
                    changeColorBtn(new_deadline, R.color.blue);
                } else {
                    new_task_Description.setError(getContext().getResources().getString(R.string.description_is_empty));
                }
            }
        });
        new_nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update current Time.
                currentTime = Calendar.getInstance().getTime();
                hour_ = currentTime.getHours() + 1;
                minute_ = 0;
                calendar_time.set(0, 0, 0, hour_, minute_);

                // Set textViews of time from time of your phone.
                setHourAndMinuteInTextViewTime();

                // Check if no view has focus:
                hideKeyboard(view);
                if (titleIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.GONE);
                    changeColorBtn(new_tags, R.color.blue);
                    dialog_deadline.setVisibility(View.VISIBLE);
                    changeColorBtn(new_deadline, R.color.blue_2);
                } else {
                    new_task_Title.setError(getContext().getResources().getString(R.string.title_is_empty));
                }
                if (descriptionIsNotEmpty()) {
                    dialog_details.setVisibility(View.GONE);
                    changeColorBtn(new_details, R.color.blue);
                    dialog_tags.setVisibility(View.GONE);
                    changeColorBtn(new_tags, R.color.blue);
                    dialog_deadline.setVisibility(View.VISIBLE);
                    changeColorBtn(new_deadline, R.color.blue_2);
                } else {
                    new_task_Description.setError(getContext().getResources().getString(R.string.description_is_empty));
                }
            }
        });

        //details
        this.new_task_Title = findViewById(R.id.new_task_editTextTextTitle);
        this.new_task_Description = findViewById(R.id.new_task_editTextTextDescription);
        //tags
        this.new_priority_low = findViewById(R.id.new_priority_low);
        this.new_priority_medium = findViewById(R.id.new_priority_medium);
        this.new_priority_high = findViewById(R.id.new_priority_high);

        //re-installer color of priority when start fab
        changeColorBtn(new_priority_low, R.color.blue);
        changeColorBtn(new_priority_medium, R.color.blue);
        changeColorBtn(new_priority_high, R.color.blue);
        this.new_priority_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "low";
                changeColorBtn(new_priority_low, R.color.low);
                changeColorBtn(new_priority_medium, R.color.blue);
                changeColorBtn(new_priority_high, R.color.blue);
            }
        });
        this.new_priority_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "medium";
                changeColorBtn(new_priority_low, R.color.blue);
                changeColorBtn(new_priority_medium, R.color.medium);
                changeColorBtn(new_priority_high, R.color.blue);
            }
        });
        this.new_priority_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "high";
                changeColorBtn(new_priority_low, R.color.blue);
                changeColorBtn(new_priority_medium, R.color.blue);
                changeColorBtn(new_priority_high, R.color.high);
            }
        });

        this.new_category_add = findViewById(R.id.new_category_add);
        this.categorys_layout = findViewById(R.id.categorys_layout);
        this.add_category_layout = findViewById(R.id.add_category_layout);
        categorys_layout.setVisibility(View.VISIBLE);
        add_category_layout.setVisibility(View.GONE);
        new_category_add.setVisibility(View.VISIBLE);
        //First color of new_category_add and icon when dialog started
        Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
        changeColorBackgroundCircleImageView(new_category_add, R.color.blue);

        myDB_category = new MyDatabaseHelper_category(getContext());
        category_id = new ArrayList<>();
        category_id_ = new ArrayList<>();
        category_name = new ArrayList<>();
        category_color = new ArrayList<>();
        category_deleted = new ArrayList<>();

        try {
            category.clear();
            storeDataCategoryInArraysAndLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }

        color_ = R.color.colorViewOne; // colorViewOne for default color

        editText_category = findViewById(R.id.editText_category);
        _editText_category = findViewById(R.id._editText_category);
        category_colors = findViewById(R.id.category_colors);
        editText_category_icon = findViewById(R.id.editText_category_icon);
        editText_category_icon_ = findViewById(R.id.editText_category_icon_);
        editText_category_done = findViewById(R.id.editText_category_done);

        category_color1 = findViewById(R.id.category_color1);
        category_color2 = findViewById(R.id.category_color2);
        category_color3 = findViewById(R.id.category_color3);
        category_color4 = findViewById(R.id.category_color4);
        category_color5 = findViewById(R.id.category_color5);
        category_color6 = findViewById(R.id.category_color6);
        category_color7 = findViewById(R.id.category_color7);

        new_category_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((categorys_layout.getVisibility() == View.VISIBLE) && (add_category_layout.getVisibility() == View.GONE)) {
                    /*Show add categories layout*/
                    categorys_layout.setVisibility(View.GONE);
                    add_category_layout.setVisibility(View.VISIBLE);
                    //change new_category_add icon

                    editText_category.setVisibility(View.VISIBLE);
                    //Start writing directly
                    editText_category.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText_category, InputMethodManager.SHOW_IMPLICIT);

                    category_colors.setVisibility(View.GONE);
                    editText_category_icon_.setVisibility(View.GONE);
                    editText_category_icon.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.icons8_delete_2).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.delete);

                } else if ((categorys_layout.getVisibility() == View.GONE) && (add_category_layout.getVisibility() == View.VISIBLE)) {
                    /*Show category*/
                    categorys_layout.setVisibility(View.VISIBLE);
                    add_category_layout.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.blue);
                }

            }
        });

        editText_category_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText_category_icon_.getVisibility() == View.GONE) {
                    editText_category_done.setVisibility(View.GONE);
                    editText_category.setVisibility(View.GONE);
                    category_colors.setVisibility(View.VISIBLE);
                    new_category_add.setVisibility(View.GONE);

                    _editText_category.setVisibility(View.GONE);
                    colorsClicked(view);
                } else if (editText_category_icon_.getVisibility() == View.VISIBLE) {
                    //Save
                    MyDatabaseHelper_category myDB = new MyDatabaseHelper_category(getContext());
                    myDB.addCategory1(editText_category.getText().toString().trim(), color_, "no");

                    category.clear();
                    storeDataCategoryInArraysAndLayout();

                    //Toast.makeText(getContext(), "Save", Toast.LENGTH_SHORT).show();

                    /*Show category*/
                    categorys_layout.setVisibility(View.VISIBLE);
                    add_category_layout.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.blue);
                }
            }
        });
        editText_category_icon_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText_category_done.setVisibility(View.GONE);
                editText_category.setVisibility(View.GONE);
                category_colors.setVisibility(View.VISIBLE);
                new_category_add.setVisibility(View.GONE);
                //colors clicked

                _editText_category.setVisibility(View.GONE);
                colorsClicked(view);

            }
        });

        //deadline
        this.linearLayoutDate = findViewById(R.id.linearLayoutDate);
        this.textViewDate = findViewById(R.id.textViewDate);
        this.linearLayoutSelectDays = findViewById(R.id.linearLayoutSelectDays);
        this.textViewSelectDays = findViewById(R.id.textViewSelectDays);
        this.linearLayoutSelectDate = findViewById(R.id.linearLayoutSelectDate);
        this.textViewStartAndEndDate = findViewById(R.id.textViewStartAndEndDate);
        this.textView_endDate = findViewById(R.id.textView_endDate);
        this.textView_startDate = findViewById(R.id.textView_startDate);
        daysRepeat();
        //date for one time
        this.textView_month = findViewById(R.id.textView_month);
        this.textView_day = findViewById(R.id.textView_day);
        this.textView_year = findViewById(R.id.textView_year);
        //date for everyday and other


        //Set textViews from date selected in calendar or horizontal scroll calendar
        calendar.setTime(date_selected);

        year_ = calendar.get(Calendar.YEAR);
        month_ = calendar.get(Calendar.MONTH);
        dayOfMonth_ = calendar.get(Calendar.DAY_OF_MONTH);

        textView_month.setText((String) DateFormat.format("MMMM", date_selected));
        textView_day.setText((String) DateFormat.format("dd", date_selected));
        textView_year.setText((String) DateFormat.format("yyyy", date_selected));

        textView_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog(textView_month, textView_day, textView_year);
            }
        });
        textView_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog(textView_month, textView_day, textView_year);
            }
        });
        textView_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog(textView_month, textView_day, textView_year);
            }
        });

        calendar_start.setTime(date_selected);
        year_start = calendar_start.get(Calendar.YEAR);
        month_start = calendar_start.get(Calendar.MONTH);
        dayOfMonth_start = calendar_start.get(Calendar.DAY_OF_MONTH);

        textView_startDate.setText((String) DateFormat.format("MMM", calendar_start) + " " + (String) DateFormat.format("dd", calendar_start) + ", " + String.valueOf(year_start));
        textView_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                calendar_start.set(year, month, dayOfMonth);
                                year_start = year;
                                month_start = month;
                                dayOfMonth_start = dayOfMonth;

                                textView_startDate.setText((String) DateFormat.format("MMM", calendar_start) + " " + (String) DateFormat.format("dd", calendar_start) + ", " + String.valueOf(year));
                                checkPossibilityOfEndDate();
                            }
                        }, year_start, month_start, dayOfMonth_start);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.updateDate(year_start, month_start, dayOfMonth_start);
                datePickerDialog.show();
            }
        });

        calendar_end.setTime(date_selected);
        System.out.println("1/from : " + String.valueOf(calendar_start.getTime()) + " to : " + String.valueOf(calendar_end.getTime()));
        calendar_end.add(Calendar.MONTH, 1);
        System.out.println("2/from : " + String.valueOf(calendar_start.getTime()) + " to : " + String.valueOf(calendar_end.getTime()));
        year_end = calendar_end.get(Calendar.YEAR);
        month_end = calendar_end.get(Calendar.MONTH);
        dayOfMonth_end = calendar_end.get(Calendar.DAY_OF_MONTH);

        textView_endDate.setText((String) DateFormat.format("MMM", calendar_end) + " " + (String) DateFormat.format("dd", calendar_end) + ", " + String.valueOf(year_end));
        textView_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                calendar_end.set(year, month, dayOfMonth);
                                year_end = year;
                                month_end = month;
                                dayOfMonth_end = dayOfMonth;

                                textView_endDate.setText((String) DateFormat.format("MMM", calendar_end) + " " + (String) DateFormat.format("dd", calendar_end) + ", " + String.valueOf(year));
                                checkPossibilityOfEndDate();
                            }
                        }, year_end, month_end, dayOfMonth_end);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.updateDate(year_end, month_end, dayOfMonth_end);
                datePickerDialog.show();
            }
        });

        this.textView_hour = findViewById(R.id.textView_hour);
        this.textView_minute = findViewById(R.id.textView_minute);
        this.textView_time_mode = findViewById(R.id.textView_time_mode);

        //Set textViews of time from time of your phone
        setHourAndMinuteInTextViewTime();

        textView_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerDialog(textView_hour, textView_minute, textView_time_mode);
            }
        });
        textView_minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerDialog(textView_hour, textView_minute, textView_time_mode);
            }
        });
        textView_time_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerDialog(textView_hour, textView_minute, textView_time_mode);
            }
        });

        this.textView_time_left = findViewById(R.id.textView_time_left);
        UpdateTimeLeftTextView();

        this.spinner_reminder_time = findViewById(R.id.spinner_reminder_time);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getContext(), R.array.reminder, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reminder_time.setAdapter(spinner_adapter);
        spinner_reminder_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //DARK MODE
                SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                String state = preferences_.getString("state", "");

                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                if (state.equals("true")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                } else if (state.equals("false")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                } else {
                    if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                        // Light (default)
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                        // Dark (default)
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    }
                }

                String spinner_text = adapterView.getItemAtPosition(position).toString();
                if (spinner_text.equals(getContext().getResources().getString(R.string.never))) {
                    reminder = 0;
                }
                if (spinner_text.equals("1 minute")) {
                    reminder = 1;
                }
                if (spinner_text.equals("2 minutes")) {
                    reminder = 2;
                }
                if (spinner_text.equals("3 minutes")) {
                    reminder = 3;
                }
                if (spinner_text.equals("5 minutes")) {
                    reminder = 5;
                }
                if (spinner_text.equals("10 minutes")) {
                    reminder = 10;
                }
                if (spinner_text.equals("15 minutes")) {
                    reminder = 15;
                }
                if (spinner_text.equals("20 minutes")) {
                    reminder = 20;
                }
                if (spinner_text.equals("25 minutes")) {
                    reminder = 25;
                }
                if (spinner_text.equals("30 minutes")) {
                    reminder = 30;
                }
                if (spinner_text.equals("45 minutes")) {
                    reminder = 45;
                }
                if (spinner_text.equals("1 hour")) {
                    reminder = 60;
                }
                if (spinner_text.equals("2 hours")) {
                    reminder = 120;
                }
                if (spinner_text.equals("3 hours")) {
                    reminder = 180;
                }
                if (spinner_text.equals("5 hours")) {
                    reminder = 300;
                }
                if (spinner_text.equals("12 hours")) {
                    reminder = 720;
                }
                if (spinner_text.equals("1 day")) {
                    reminder = 1440;
                }
                if (spinner_text.equals("2 days")) {
                    reminder = 2880;
                }
                if (spinner_text.equals("3 days")) {
                    reminder = 4320;
                }
                if (spinner_text.equals("5 days")) {
                    reminder = 7200;
                }
                if (spinner_text.equals("1 week")) {
                    reminder = 10080;
                }
                if (spinner_text.equals("2 weeks")) {
                    reminder = 20160;
                }
                if (spinner_text.equals("3 weeks")) {
                    reminder = 30240;
                }
                if (spinner_text.equals("4 weeks")) {
                    reminder = 40320;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.spinner_repeat_time = findViewById(R.id.spinner_repeat_time);
        ArrayAdapter<CharSequence> spinner_adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.repeat, android.R.layout.simple_spinner_item);
        spinner_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_repeat_time.setAdapter(spinner_adapter1);
        spinner_repeat_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //DARK MODE
                SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                String state = preferences_.getString("state", "");

                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                if (state.equals("true")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                } else if (state.equals("false")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                } else {
                    if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                        // Light (default)
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                        // Dark (default)
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    }
                }

                String spinner_text = adapterView.getItemAtPosition(position).toString();
                if (spinner_text.equals("One time")) {
                    linearLayoutDate.setVisibility(View.VISIBLE);
                    textViewDate.setVisibility(View.VISIBLE);
                    linearLayoutSelectDays.setVisibility(View.GONE);
                    textViewSelectDays.setVisibility(View.GONE);
                    linearLayoutSelectDate.setVisibility(View.GONE);
                    textViewStartAndEndDate.setVisibility(View.GONE);
                    textView_endDate.setVisibility(View.GONE);
                    textView_startDate.setVisibility(View.GONE);
                    textView_time_left.setVisibility(View.VISIBLE);
                    repeat = 0;
                }
                if (spinner_text.equals("Everyday")) {
                    linearLayoutDate.setVisibility(View.GONE);
                    textViewDate.setVisibility(View.GONE);
                    linearLayoutSelectDays.setVisibility(View.GONE);
                    textViewSelectDays.setVisibility(View.GONE);
                    linearLayoutSelectDate.setVisibility(View.VISIBLE);
                    textViewStartAndEndDate.setVisibility(View.VISIBLE);
                    textView_endDate.setVisibility(View.VISIBLE);
                    textView_startDate.setVisibility(View.VISIBLE);
                    textView_time_left.setVisibility(View.GONE);
                    repeat = 1;
                }
                if (spinner_text.equals("Select days")) {
                    linearLayoutDate.setVisibility(View.GONE);
                    textViewDate.setVisibility(View.GONE);
                    linearLayoutSelectDays.setVisibility(View.VISIBLE);
                    textViewSelectDays.setVisibility(View.VISIBLE);
                    linearLayoutSelectDate.setVisibility(View.VISIBLE);
                    textViewStartAndEndDate.setVisibility(View.VISIBLE);
                    textView_endDate.setVisibility(View.VISIBLE);
                    textView_startDate.setVisibility(View.VISIBLE);
                    textView_time_left.setVisibility(View.GONE);
                    repeat = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.new_saveBtn = findViewById(R.id.new_saveBtn);
    }

    private void checkPossibilityOfEndDate() {
        if ((calendar_end.getTimeInMillis() - calendar_start.getTimeInMillis() < 432000000L) || (calendar_end.getTimeInMillis() - calendar_start.getTimeInMillis() > 31536000000L)) {// 5 Days >  > 1 year
            findViewById(R.id.constraintLayout_endDate).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_stroke_red_spinners));
            textView_endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            Glide.with(getContext()).load(ContextCompat.getDrawable(getContext(), R.drawable.ic_icons8_expand_arrow_red)).into((ImageView) findViewById(R.id.ImageView_endDate));
        } else {
            findViewById(R.id.constraintLayout_endDate).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_stroke_black_spinners));
            //DARK MODE
            SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
            String state = preferences_.getString("state", "");
            if (state.equals("true")) {
                textView_endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
            } else if (state.equals("false")) {
                textView_endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
            } else {
                if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                    // Light (default)
                    textView_endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                    // Dark (default)
                    textView_endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
            }
            Glide.with(getContext()).load(ContextCompat.getDrawable(getContext(), R.drawable.ic_icons8_expand_arrow)).into((ImageView) findViewById(R.id.ImageView_endDate));
        }
    }

    private void daysRepeat() {
        days_sunday = findViewById(R.id.days_sunday);
        days_monday = findViewById(R.id.days_monday);
        days_tuesday = findViewById(R.id.days_tuesday);
        days_wednesday = findViewById(R.id.days_wednesday);
        days_thursday = findViewById(R.id.days_thursday);
        days_friday = findViewById(R.id.days_friday);
        days_saturday = findViewById(R.id.days_saturday);

        days_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sunday) {
                    days_sunday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_sunday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_sunday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                sunday = !sunday;
            }
        });

        days_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monday) {
                    days_monday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_monday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_monday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                monday = !monday;
            }
        });

        days_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tuesday) {
                    days_tuesday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_tuesday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_tuesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                tuesday = !tuesday;
            }
        });

        days_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wednesday) {
                    days_wednesday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    } else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_wednesday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_wednesday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                wednesday = !wednesday;
            }
        });

        days_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thursday) {
                    days_thursday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    }else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_thursday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_thursday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                thursday = !thursday;
            }
        });

        days_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friday) {
                    days_friday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    }else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_friday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_friday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                friday = !friday;
            }
        });

        days_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saturday) {
                    days_saturday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days));
                    days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    //DARK MODE
                    SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");
                    if (state.equals("true")) {
                        days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                    } else if (state.equals("false")) {
                        days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    }else {
                        if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.blue)) {
                            // Light (default)
                            days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        } else if (new_task_Title.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.white_)) {
                            // Dark (default)
                            days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                        }
                    }
                } else {
                    days_saturday.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_days_full));
                    days_saturday.setTextColor(ContextCompat.getColor(getContext(), R.color.white_));
                }
                saturday = !saturday;
            }
        });
    }

    private void UpdateTimeLeftTextView() {
        calNow = Calendar.getInstance();
        calChose = Calendar.getInstance();

        calChose.set(year_, month_, dayOfMonth_, hour_, minute_, 0);

        long time_now = calNow.getTimeInMillis();
        long time_chose = calChose.getTimeInMillis();

        long time_left = time_chose - time_now;

        //Toast.makeText(getContext(),String.valueOf(time_left), Toast.LENGTH_SHORT).show();

        if (time_left > 1000) {
            if (time_left > 60000) {
                if (time_left > 3600000) {
                    if (time_left > 86400000) {
                        if (time_left > 604800000) {
                            if (time_left > 2628288000L) {
                                if (time_left > 31536000000L) {
                                    // Years
                                    if (((time_left % 31536000000L) / 2628288000L) == 0) {
                                        textView_time_left.setText(String.valueOf(time_left / 31536000000L) + getContext().getResources().getString(R.string._year_left));
                                    } else {
                                        textView_time_left.setText(String.valueOf(time_left / 31536000000L) + getContext().getResources().getString(R.string._year_) + String.valueOf((time_left % 31536000000L) / 2628288000L) + getContext().getResources().getString(R.string._month_left));
                                    }
                                } else {
                                    // Months
                                    if (((time_left % 2628288000l) / 86400000) == 0) {
                                        textView_time_left.setText(String.valueOf(time_left / 2628288000l) + getContext().getResources().getString(R.string._month_left));
                                    } else {
                                        textView_time_left.setText(String.valueOf(time_left / 2628288000l) + getContext().getResources().getString(R.string._month_) + String.valueOf((time_left % 2628288000l) / 86400000) + getContext().getResources().getString(R.string._d_left));
                                    }
                                }
                            } else {
                                // Weeks we show the number of day not week.
                                if (((time_left % 86400000) / 3600000) == 0) {
                                    textView_time_left.setText(String.valueOf(time_left / 86400000) + getContext().getResources().getString(R.string._d_left));
                                } else {
                                    textView_time_left.setText(String.valueOf(time_left / 86400000) + getContext().getResources().getString(R.string._d_) + String.valueOf((time_left % 86400000) / 3600000) + getContext().getResources().getString(R.string._h_left));
                                }
                            }
                        } else {
                            // Days
                            if (((time_left % 86400000) / 3600000) == 0) {
                                textView_time_left.setText(String.valueOf(time_left / 86400000) + getContext().getResources().getString(R.string._d_left));
                            } else {
                                textView_time_left.setText(String.valueOf(time_left / 86400000) + getContext().getResources().getString(R.string._d_) + String.valueOf((time_left % 86400000) / 3600000) + getContext().getResources().getString(R.string._h_left));
                            }
                        }
                    } else {
                        // Hours
                        if (((time_left % 3600000) / 60000) == 0) {
                            textView_time_left.setText(String.valueOf(time_left / 3600000) + getContext().getResources().getString(R.string._h_left));
                        } else {
                            textView_time_left.setText(String.valueOf(time_left / 3600000) + getContext().getResources().getString(R.string._h_) + String.valueOf((time_left % 3600000) / 60000) + getContext().getResources().getString(R.string._min_left));
                        }
                    }
                } else {
                    // Minutes
                    if (((time_left % 60000) / 1000) == 0) {
                        textView_time_left.setText(String.valueOf(time_left / 60000) + getContext().getResources().getString(R.string._min_left));
                    } else {
                        textView_time_left.setText(String.valueOf(time_left / 60000) + getContext().getResources().getString(R.string._min_) + String.valueOf((time_left % 60000) / 1000) + getContext().getResources().getString(R.string._sec_left));
                    }
                }
            } else {
                // Seconds
                textView_time_left.setText(String.valueOf(time_left / 1000) + getContext().getResources().getString(R.string._sec_left));
            }
        } else {
            if (time_left >= -60000) {
                textView_time_left.setText(getContext().getResources().getString(R.string.now));
            } else {
                textView_time_left.setText(getContext().getResources().getString(R.string.time_is_less_than_now));
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateTimeLeftTextView();
            }
        }, 1000);
    }

    private void setHourAndMinuteInTextViewTime() {

        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        if (hour_ == 12) {
            textView_hour.setText(String.valueOf(HH.format(calendar_time.getTime())));
        } else {
            textView_hour.setText(String.valueOf(KK.format(calendar_time.getTime())));
        }
        textView_minute.setText(String.valueOf(mm.format(calendar_time.getTime())));
        textView_time_mode.setText(String.valueOf(aa.format(calendar_time.getTime())));

        time = String.valueOf(HH.format(calendar_time.getTime())) + ":" + String.valueOf(mm.format(calendar_time.getTime()));

    }

    private boolean titleIsNotEmpty() {
        if (new_task_Title.getText().toString().trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean descriptionIsNotEmpty() {
        if (new_task_Description.getText().toString().trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void startTimePickerDialog(TextView textView_hour, TextView textView_minute, TextView textView_time_mode) {

        //Open TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
                        hour_ = hourOfDay;
                        minute_ = min;

                        SimpleDateFormat KK = new SimpleDateFormat("KK");
                        SimpleDateFormat HH = new SimpleDateFormat("HH");
                        SimpleDateFormat mm = new SimpleDateFormat("mm");
                        SimpleDateFormat aa = new SimpleDateFormat("aa");


                        calendar_time.set(0, 0, 0, hour_, minute_);
                        if (hour_ == 12) {
                            textView_hour.setText(String.valueOf(HH.format(calendar_time.getTime())));
                        } else {
                            textView_hour.setText(String.valueOf(KK.format(calendar_time.getTime())));
                        }
                        textView_minute.setText(String.valueOf(mm.format(calendar_time.getTime())));
                        textView_time_mode.setText(String.valueOf(aa.format(calendar_time.getTime())));

                        time = String.valueOf(HH.format(calendar_time.getTime())) + ":" + String.valueOf(mm.format(calendar_time.getTime()));

                    }
                }, 12, 0, false
        );
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(hour_, minute_);
        timePickerDialog.show();
    }

    private void startDatePickerDialog(TextView textView_month, TextView textView_day, TextView textView_year) {

        //Open DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        year_ = year;
                        month_ = month;
                        dayOfMonth_ = dayOfMonth;

                        textView_day.setText(String.valueOf(dayOfMonth));
                        textView_year.setText(String.valueOf(year));
                        textView_month.setText((String) DateFormat.format("MMMM", calendar));
                    }
                }, year_, month_, dayOfMonth_);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.updateDate(year_, month_, dayOfMonth_);
        datePickerDialog.show();
    }

    private void storeDataCategoryInArraysAndLayout() {
        category_id.clear();
        category_id_.clear();
        category_name.clear();
        category_color.clear();
        category_deleted.clear();

        Cursor cursor = myDB_category.readAllData();
        while (cursor.moveToNext()) {
            category_id.add(cursor.getString(0));
            category_id_.add(cursor.getString(1));
            category_name.add(cursor.getString(2));
            category_color.add(cursor.getString(3));
            category_deleted.add(cursor.getString(4));
        }

        category_btn = new Button[cursor.getCount()];
        categorys_layout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 72);
        params.setMarginEnd(8);

        for (int i = 0; i < category_btn.length; i++) {
            if (category_deleted.get(i).equals("no")) {

                category_btn[i] = new Button(getContext());
                category_btn[i].setText(category_name.get(i).toString());
                category_btn[i].setTextSize(12);
                category_btn[i].setAllCaps(false);
                category_btn[i].setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                category_btn[i].setBackgroundResource(R.drawable.custom_button_priority);
                changeColorBtn(category_btn[i], Integer.parseInt(category_color.get(i)));
                category_btn[i].setLayoutParams(params);

                categorys_layout.addView(category_btn[i]);

                int finalI = i;
                category_btn[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (category_btn[finalI].getText().equals(category_name.get(finalI).toString() + "\t " + Html.fromHtml("&#10003;"))) {
                            category_btn[finalI].setText(category_name.get(finalI).toString());
                            //remove from category list
                            category.remove(category_id_.get(finalI).toString());
                        } else {
                            if (category.size() < 5) {
                                //add to category list
                                category_btn[finalI].setText(category_name.get(finalI).toString() + "\t " + Html.fromHtml("&#10003;"));
                                category.add(category_id_.get(finalI).toString());
                            } else {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.select_at_most_five_categories), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                category_btn[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                        title.setText(getContext().getResources().getString(R.string.delete_this_category) + " :\n\"" + String.valueOf(category_btn[finalI].getText()) + "\" ?");
                        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                myDB_category.updateData(category_id.get(finalI), category_id_.get(finalI), category_name.get(finalI), Integer.parseInt(category_color.get(finalI)), "yes");
                                storeDataCategoryInArraysAndLayout();

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);

                        return true;//true for show just the long click listener
                    }
                });
            }
        }
    }

    private void colorsClicked(View view) {

        category_color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewOne;
                changeColorCategory(color_);
            }
        });
        category_color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewTwo;
                changeColorCategory(color_);
            }
        });
        category_color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewThree;
                changeColorCategory(color_);
            }
        });
        category_color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewFour;
                changeColorCategory(color_);
            }
        });
        category_color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewFive;
                changeColorCategory(color_);
            }
        });
        category_color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewSix;
                changeColorCategory(color_);
            }
        });
        category_color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewSeven;
                changeColorCategory(color_);
            }
        });
    }

    private void changeColorCategory(int color_) {
        editText_category_icon_.setVisibility(View.VISIBLE);
        editText_category_icon.setVisibility(View.VISIBLE);

        editText_category_done.setVisibility(View.VISIBLE);
        new_category_add.setVisibility(View.VISIBLE);
        category_colors.setVisibility(View.GONE);

        editText_category.setVisibility(View.VISIBLE);
        editText_category.setTextColor(ContextCompat.getColor(getContext(), color_));
        editText_category.setHintTextColor(ContextCompat.getColor(getContext(), color_));

        editText_category_icon_.setBackgroundResource(color_);
        changeColorBackground(_editText_category, color_);
        _editText_category.setVisibility(View.VISIBLE);
    }

    private void changeColorBackground(LinearLayout _editText_category, int color_) {
        GradientDrawable background = (GradientDrawable) _editText_category.getBackground();

        background.mutate();
        background.setStroke(1, ContextCompat.getColor(getContext(), color_));
    }

    public void build() {
        show();
    }

    public void changeColorBackgroundCircleImageView(CircleImageView imageView, int color) {
        Drawable background = imageView.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(getContext(), color));
        }
    }

    public void changeColorBtn(Button btn, int color) {
        Drawable background = btn.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(getContext(), color));
        }
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getDate() {
        /** Month_ start with 0 */
        return String.valueOf(year_) + "-" + String.valueOf(month_) + "-" + String.valueOf(dayOfMonth_);
    }

    public String getStartDate() {
        return String.valueOf(year_start) + "-" + String.valueOf(month_start) + "-" + String.valueOf(dayOfMonth_start);
    }

    public String getEndDate() {
        return String.valueOf(year_end) + "-" + String.valueOf(month_end) + "-" + String.valueOf(dayOfMonth_end);
    }

    public List<Integer> getDays() {
        List<Integer> days = new ArrayList<>();

        if (sunday) {
            days.add(Calendar.SUNDAY);
        }
        if (monday) {
            days.add(Calendar.MONDAY);
        }
        if (tuesday) {
            days.add(Calendar.TUESDAY);
        }
        if (wednesday) {
            days.add(Calendar.WEDNESDAY);
        }
        if (thursday) {
            days.add(Calendar.THURSDAY);
        }
        if (friday) {
            days.add(Calendar.FRIDAY);
        }
        if (saturday) {
            days.add(Calendar.SATURDAY);
        }

        return days;
    }

    public EditText getEditTextTitle() {
        return new_task_Title;
    }

    public EditText getEditTextDescription() {
        return new_task_Description;
    }

    public String getTitle() {
        title = new_task_Title.getText().toString().trim();
        return title;
    }

    public String getDescription() {
        description = new_task_Description.getText().toString().trim();
        return description;
    }

    public String getPriority() {
        if (priority == null) {
            priority = "default";
        }
        return priority;
    }

    public String getCategory() {
        if (category == null) {
            return "default";
        } else {
            return String.valueOf(category);
        }
    }

    public String getTime() {
        return time;
    }

    public int getReminder() {
        if (reminder == -1) {
            return 0;
        } else {
            return reminder;
        }
    }

    public int getRepeat() {
        return repeat;
    }

    public Button getNew_saveBtn() {
        return new_saveBtn;
    }

}