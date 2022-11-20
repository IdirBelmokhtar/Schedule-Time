package com.ecommerce.scheduletime.HomeActivity;

import static com.ecommerce.scheduletime.Fragments.CalendarFragment.calendarView;
import static com.ecommerce.scheduletime.Fragments.ListFragment.calendarViewHorizontal_layout;
import static com.ecommerce.scheduletime.Fragments.ListFragment.endDate;
import static com.ecommerce.scheduletime.Fragments.ListFragment.horizontalCalendar;
import static com.ecommerce.scheduletime.Fragments.ListFragment.startDate;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.fOpen;
import static com.ecommerce.scheduletime.SQLite.MyDatabaseHelper.TASK_NEW_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ecommerce.scheduletime.Dialog.BottomDialogCategory;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTaskActivity extends AppCompatActivity {

    private Toolbar toolbar;

    MyDatabaseHelper_category myDB_category;
    String category_id, category_name, category_color, category_deleted;
    Button[] category_btn;
    LinearLayout categorys_layout_;
    CircleImageView plus_category_add;
    List<String> category_ids = new ArrayList<>();
    public static List<String> category_ids_new = new ArrayList<>();

    private String id;
    String date, title, description, priority, category, time, done, reminder;
    TextView textView_month, textView_day, textView_year, textView_hour, textView_minute, textView_time_mode;
    Spinner spinner_reminder_time;

    private EditText edit_title, edit_description;
    private TextView edit_title_length;
    private Button edit_priority_low, edit_priority_medium, edit_priority_high;

    List<String> _time = new ArrayList<>();
    List<String> _date = new ArrayList<>();
    int reminder_ = -1;

    int year_, month_, dayOfMonth_, hour_, minute_;
    Calendar calendar_ = Calendar.getInstance();
    Calendar calendar_time = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Toolbar
        toolbar = findViewById(R.id.toolbar_editTask);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViews();

        id = String.valueOf(getIntent().getStringExtra("id"));

        MyDatabaseHelper myDB = new MyDatabaseHelper(EditTaskActivity.this);
        Cursor cursor = myDB.readData(id);
        while (cursor.moveToNext()) {
            id = (cursor.getString(0));
            date = (cursor.getString(1));
            title = (cursor.getString(2));
            description = (cursor.getString(3));
            priority = (cursor.getString(4));
            category = (cursor.getString(5));
            time = (cursor.getString(6));
            done = (cursor.getString(7));
            reminder = (cursor.getString(8));
        }

        reminder_ = Integer.parseInt(reminder);

        spinnerReminder();

        _time = Arrays.asList(time.split(":"));
        _date = Arrays.asList(date.split("-"));

        // Title and Description.
        edit_title.setText(title);
        edit_description.setText(description);
        edit_title_length.setText(String.valueOf(edit_title.getText().length()) + "/50");
        edit_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    edit_title_length.setText(String.valueOf(edit_title.getText().length()) + "/50");
                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Priority.
        if (priority.equals("default")) {
            changeColorBtn(edit_priority_low, R.color.blue);
            changeColorBtn(edit_priority_medium, R.color.blue);
            changeColorBtn(edit_priority_high, R.color.blue);
        } else if (priority.equals("low")) {
            changeColorBtn(edit_priority_low, R.color.low);
            changeColorBtn(edit_priority_medium, R.color.blue);
            changeColorBtn(edit_priority_high, R.color.blue);
        } else if (priority.equals("medium")) {
            changeColorBtn(edit_priority_low, R.color.blue);
            changeColorBtn(edit_priority_medium, R.color.medium);
            changeColorBtn(edit_priority_high, R.color.blue);
        } else if (priority.equals("high")) {
            changeColorBtn(edit_priority_low, R.color.blue);
            changeColorBtn(edit_priority_medium, R.color.blue);
            changeColorBtn(edit_priority_high, R.color.high);
        }

        this.edit_priority_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "low";
                changeColorBtn(edit_priority_low, R.color.low);
                changeColorBtn(edit_priority_medium, R.color.blue);
                changeColorBtn(edit_priority_high, R.color.blue);
            }
        });
        this.edit_priority_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "medium";
                changeColorBtn(edit_priority_low, R.color.blue);
                changeColorBtn(edit_priority_medium, R.color.medium);
                changeColorBtn(edit_priority_high, R.color.blue);
            }
        });
        this.edit_priority_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priority = "high";
                changeColorBtn(edit_priority_low, R.color.blue);
                changeColorBtn(edit_priority_medium, R.color.blue);
                changeColorBtn(edit_priority_high, R.color.high);
            }
        });

        // Category.
        categorys_layout_ = findViewById(R.id.categorys_layout_);
        plus_category_add = findViewById(R.id.plus_category_add);
        myDB_category = new MyDatabaseHelper_category(EditTaskActivity.this);
        if (!category.equals("[]")) {
            category_ids = Arrays.asList(removeLastString(category.substring(1)).split(", "));
            category_ids_new.clear();
            category_ids_new.addAll(category_ids);
        }
        storeDataCategoryInArraysAndLayout(categorys_layout_, category);
        plus_category_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomDialogCategory bottomDialogCategory = new BottomDialogCategory((Activity) EditTaskActivity.this);

                bottomDialogCategory.build();
                bottomDialogCategory.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomDialogCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bottomDialogCategory.getWindow().setWindowAnimations(R.style.SheetDialogAnimation);
                bottomDialogCategory.getWindow().setGravity(Gravity.BOTTOM);

                bottomDialogCategory.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        category = String.valueOf(category_ids_new);
                        storeDataCategoryInArraysAndLayout(categorys_layout_, category);
                    }
                });
            }
        });

        // Date and Time.
        setDateAndTimeInTextViewTime();
        // OnClick Date and Time.
        findViewById(R.id.linearLayoutDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog(textView_month, textView_day, textView_year);
            }
        });
        findViewById(R.id.linearLayoutTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerDialog(textView_hour, textView_minute, textView_time_mode);
            }
        });

    }

    private void spinnerReminder() {
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(EditTaskActivity.this, R.array.reminder, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reminder_time.setAdapter(spinner_adapter);

        if (reminder.equals("0")) {
            spinner_reminder_time.setSelection(0);
        }
        if (reminder.equals("1")) {
            spinner_reminder_time.setSelection(1);
        }
        if (reminder.equals("2")) {
            spinner_reminder_time.setSelection(2);
        }
        if (reminder.equals("3")) {
            spinner_reminder_time.setSelection(3);
        }
        if (reminder.equals("5")) {
            spinner_reminder_time.setSelection(4);
        }
        if (reminder.equals("10")) {
            spinner_reminder_time.setSelection(5);
        }
        if (reminder.equals("15")) {
            spinner_reminder_time.setSelection(6);
        }
        if (reminder.equals("20")) {
            spinner_reminder_time.setSelection(7);
        }
        if (reminder.equals("25")) {
            spinner_reminder_time.setSelection(8);
        }
        if (reminder.equals("30")) {
            spinner_reminder_time.setSelection(9);
        }
        if (reminder.equals("45")) {
            spinner_reminder_time.setSelection(10);
        }
        if (reminder.equals("60")) {
            spinner_reminder_time.setSelection(11);
        }
        if (reminder.equals("120")) {
            spinner_reminder_time.setSelection(12);
        }
        if (reminder.equals("180")) {
            spinner_reminder_time.setSelection(13);
        }
        if (reminder.equals("300")) {
            spinner_reminder_time.setSelection(14);
        }
        if (reminder.equals("720")) {
            spinner_reminder_time.setSelection(15);
        }
        if (reminder.equals("1440")) {
            spinner_reminder_time.setSelection(16);
        }
        if (reminder.equals("2880")) {
            spinner_reminder_time.setSelection(17);
        }
        if (reminder.equals("4320")) {
            spinner_reminder_time.setSelection(18);
        }
        if (reminder.equals("7200")) {
            spinner_reminder_time.setSelection(19);
        }
        if (reminder.equals("10080")) {
            spinner_reminder_time.setSelection(20);
        }
        if (reminder.equals("20160")) {
            spinner_reminder_time.setSelection(21);
        }
        if (reminder.equals("30240")) {
            spinner_reminder_time.setSelection(22);
        }
        if (reminder.equals("40320")) {
            spinner_reminder_time.setSelection(23);
        }

        spinner_reminder_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //DARK MODE
                SharedPreferences preferences_ = getSharedPreferences("dark_mode", MODE_PRIVATE);
                String state = preferences_.getString("state", "");

                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(EditTaskActivity.this, R.color.blue));
                if (state.equals("true")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(EditTaskActivity.this, R.color.white_));
                } else if (state.equals("false")) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(EditTaskActivity.this, R.color.blue));
                }

                String spinner_text = adapterView.getItemAtPosition(position).toString();
                if (spinner_text.equals("Never")) {
                    reminder_ = 0;
                }
                if (spinner_text.equals("1 minute")) {
                    reminder_ = 1;
                }
                if (spinner_text.equals("2 minutes")) {
                    reminder_ = 2;
                }
                if (spinner_text.equals("3 minutes")) {
                    reminder_ = 3;
                }
                if (spinner_text.equals("5 minutes")) {
                    reminder_ = 5;
                }
                if (spinner_text.equals("10 minutes")) {
                    reminder_ = 10;
                }
                if (spinner_text.equals("15 minutes")) {
                    reminder_ = 15;
                }
                if (spinner_text.equals("20 minutes")) {
                    reminder_ = 20;
                }
                if (spinner_text.equals("25 minutes")) {
                    reminder_ = 25;
                }
                if (spinner_text.equals("30 minutes")) {
                    reminder_ = 30;
                }
                if (spinner_text.equals("45 minutes")) {
                    reminder_ = 45;
                }
                if (spinner_text.equals("1 hour")) {
                    reminder_ = 60;
                }
                if (spinner_text.equals("2 hours")) {
                    reminder_ = 120;
                }
                if (spinner_text.equals("3 hours")) {
                    reminder_ = 180;
                }
                if (spinner_text.equals("5 hours")) {
                    reminder_ = 300;
                }
                if (spinner_text.equals("12 hours")) {
                    reminder_ = 720;
                }
                if (spinner_text.equals("1 day")) {
                    reminder_ = 1440;
                }
                if (spinner_text.equals("2 days")) {
                    reminder_ = 2880;
                }
                if (spinner_text.equals("3 days")) {
                    reminder_ = 4320;
                }
                if (spinner_text.equals("5 days")) {
                    reminder_ = 7200;
                }
                if (spinner_text.equals("1 week")) {
                    reminder_ = 10080;
                }
                if (spinner_text.equals("2 weeks")) {
                    reminder_ = 20160;
                }
                if (spinner_text.equals("3 weeks")) {
                    reminder_ = 30240;
                }
                if (spinner_text.equals("4 weeks")) {
                    reminder_ = 40320;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void storeDataCategoryInArraysAndLayout(LinearLayout category_layout_, String task_category) {
        if (category_ids_new.size() < 5){
            plus_category_add.setVisibility(View.VISIBLE);
        }else {
            plus_category_add.setVisibility(View.GONE);
        }
            //category_ids = Arrays.asList(removeLastString(task_category.substring(1)).split(", "));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 108);
            params.setMarginEnd(8);

            category_btn = new Button[category_ids_new.size()];//category_ids.size
            category_layout_.removeAllViews();

            for (int i = 0; i < category_btn.length; i++) {

                Cursor cursor = myDB_category.readData(category_ids_new.get(i));
                while (cursor.moveToNext()) {
                    category_id = (cursor.getString(0));
                    category_name = (cursor.getString(1));
                    category_color = (cursor.getString(2));
                    category_deleted = (cursor.getString(3));
                }

                category_btn[i] = new Button(EditTaskActivity.this);
                category_btn[i].setText(category_name.toString());
                category_btn[i].setTextSize(12);
                category_btn[i].setAllCaps(false);
                category_btn[i].setTextColor(ContextCompat.getColor(EditTaskActivity.this, R.color.blue));
                category_btn[i].setBackgroundResource(R.drawable.custom_button_priority);
                changeColorBtn(category_btn[i], Integer.parseInt(category_color));
                category_btn[i].setLayoutParams(params);

                category_layout_.addView(category_btn[i]);

                int finalI = i;
                category_btn[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final Dialog dialog = new Dialog(EditTaskActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);
                        TextView sheet_dialog_delete_text = dialog.findViewById(R.id.sheet_dialog_delete_text);
                        sheet_dialog_delete_text.setText("Remove");

                        title.setText("Remove this category from task :\n\"" + String.valueOf(category_btn[finalI].getText()) + "\" ?");
                        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                category_ids_new.remove(finalI);
                                category = String.valueOf(category_ids_new);

                                storeDataCategoryInArraysAndLayout(categorys_layout_, category);

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        return true;
                    }
                });
            }
    }

    private String removeLastString(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    private void findViews() {
        edit_title = findViewById(R.id.edit_title);
        edit_description = findViewById(R.id.edit_description);
        edit_title_length = findViewById(R.id.edit_title_length);
        edit_priority_low = findViewById(R.id.edit_priority_low);
        edit_priority_medium = findViewById(R.id.edit_priority_medium);
        edit_priority_high = findViewById(R.id.edit_priority_high);
        textView_hour = findViewById(R.id.textView_hour);
        textView_minute = findViewById(R.id.textView_minute);
        textView_time_mode = findViewById(R.id.textView_time_mode);
        textView_month = findViewById(R.id.textView_month);
        textView_day = findViewById(R.id.textView_day);
        textView_year = findViewById(R.id.textView_year);
        spinner_reminder_time = findViewById(R.id.spinner_reminder_time);

    }

    private void setDateAndTimeInTextViewTime() {

        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        calendar_time.set(Integer.parseInt(_date.get(0)), Integer.parseInt(_date.get(1)), Integer.parseInt(_date.get(2)), Integer.parseInt(_time.get(0)), Integer.parseInt(_time.get(1)));

        textView_month.setText((String) DateFormat.format("MMMM", calendar_time.getTime()));
        textView_day.setText((String) DateFormat.format("dd", calendar_time.getTime()));
        textView_year.setText((String) DateFormat.format("yyyy", calendar_time.getTime()));

        if (Integer.parseInt(_time.get(0)) == 12) {
            textView_hour.setText(String.valueOf(HH.format(calendar_time.getTime())));
        } else {
            textView_hour.setText(String.valueOf(KK.format(calendar_time.getTime())));
        }
        textView_minute.setText(String.valueOf(mm.format(calendar_time.getTime())));
        textView_time_mode.setText(String.valueOf(aa.format(calendar_time.getTime())));

        time = String.valueOf(HH.format(calendar_time.getTime())) + ":" + String.valueOf(mm.format(calendar_time.getTime()));

        year_ = Integer.parseInt(_date.get(0));
        month_ = Integer.parseInt(_date.get(1));
        dayOfMonth_ = Integer.parseInt(_date.get(2));
        hour_ = Integer.parseInt(_time.get(0));
        minute_ = Integer.parseInt(_time.get(1));

        calendar_.set(year_, month_, dayOfMonth_, 0, 0, 0);
        calendar_time.set(0, 0, 0, hour_, minute_);

    }

    private void startTimePickerDialog(TextView textView_hour, TextView textView_minute, TextView textView_time_mode) {

        //Open TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                EditTaskActivity.this,
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
                EditTaskActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        calendar_.set(year, month, dayOfMonth);
                        year_ = year;
                        month_ = month;
                        dayOfMonth_ = dayOfMonth;

                        textView_day.setText(String.valueOf(dayOfMonth));
                        textView_year.setText(String.valueOf(year));
                        textView_month.setText((String) DateFormat.format("MMMM", calendar_));
                    }
                }, year_, month_, dayOfMonth_);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.updateDate(year_, month_, dayOfMonth_);
        datePickerDialog.show();
    }

    public void changeColorBtn(Button btn, int color) {
        Drawable background = btn.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(EditTaskActivity.this, color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(EditTaskActivity.this, color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(EditTaskActivity.this, color));
        }
    }

    public void edit_task_change(View view) {
        String id_ = id;
        String date_ = String.valueOf(year_) + "-" + String.valueOf(month_) + "-" + String.valueOf(dayOfMonth_); /** Month_ start with 0 */
        String title_ = edit_title.getText().toString().trim();
        String description_ = edit_description.getText().toString().trim();
        String priority_ = priority;
        String category_ = category;
        String time_ = String.valueOf((String) DateFormat.format("HH", calendar_time.getTime())) + ":" + String.valueOf((String) DateFormat.format("mm", calendar_time.getTime()));
        String done_ = done;
        int reminder__ = reminder_;

        MyDatabaseHelper myDB = new MyDatabaseHelper(EditTaskActivity.this);
        myDB.updateData(id_, date_, title_, description_, priority_, category_, time_, done_, reminder__);

        TASK_NEW_ID = Integer.parseInt(id_);
        if (fOpen == 1) {
            if (calendar_.getTimeInMillis() < startDate.getTimeInMillis() || endDate.getTimeInMillis() < calendar_.getTimeInMillis()) {
                calendarViewHorizontal_layout.setVisibility(View.GONE);
            } else {
                calendarViewHorizontal_layout.setVisibility(View.VISIBLE);
            }
            horizontalCalendar.selectDate(calendar_, true);
        } else if (fOpen == 2) {
            calendarView.scrollToCalendar(year_, month_ + 1, dayOfMonth_, true);
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        category_ids_new.clear();
    }
}