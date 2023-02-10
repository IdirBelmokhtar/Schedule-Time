package com.ecommerce.scheduletime.Adapter;

import static android.content.Context.MODE_PRIVATE;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.currentTime;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.fOpen;
import static com.ecommerce.scheduletime.HomeActivity.MainActivity.scrollToNewTask;
import static com.ecommerce.scheduletime.SQLite.MyDatabaseHelper.TASK_NEW_ID;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.scheduletime.Dialog.BottomDialogOther;
import com.ecommerce.scheduletime.Dialog.DialogDetails;
import com.ecommerce.scheduletime.Dialog.DialogNewTask;
import com.ecommerce.scheduletime.HomeActivity.EditTaskActivity;
import com.ecommerce.scheduletime.Fragments.CalendarFragment;
import com.ecommerce.scheduletime.Fragments.ListFragment;
import com.ecommerce.scheduletime.Model.Tasks;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;
import com.ecommerce.scheduletime.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecyclerViewTasksAdapter extends RecyclerView.Adapter<RecyclerViewTasksAdapter.RecyclerViewTasksHolder> {

    int color_task_title;

    private List<Tasks> mTasks;
    private Context context;

    MyDatabaseHelper_category myDB_category;
    String category_id, category_name, category_color, category_deleted;
    TextView[] mDots_category;
    Button[] category_btn;

    List<String> category_ids = new ArrayList<>();

    public static int idNewTaskPosition = 0;

    public RecyclerViewTasksAdapter(List<Tasks> mTasks, Context context) {
        this.mTasks = mTasks;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewTasksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recycler_view_tasks, parent, false);
        return new RecyclerViewTasksHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTasksHolder holder, int position) {
        Tasks task = mTasks.get(position);

        try {
            int finalPosition = position;
            if (TASK_NEW_ID != 0) {
                if (TASK_NEW_ID == Integer.parseInt(task.getTask_id())) {
                    idNewTaskPosition = finalPosition;

                    //DARK MODE
                    SharedPreferences preferences_ = context.getSharedPreferences("dark_mode", MODE_PRIVATE);
                    String state = preferences_.getString("state", "");

                    color_task_title = holder.task_title.getCurrentTextColor();

                    int colorFrom = context.getResources().getColor(R.color.black_overlay);
                    int colorTo = context.getResources().getColor(R.color.blue_);

                    if (state.equals("true")) {
                        colorFrom = context.getResources().getColor(R.color.black_overlay);
                        colorTo = context.getResources().getColor(R.color.dark);
                    } else if (state.equals("false")) {
                        colorFrom = context.getResources().getColor(R.color.black_overlay);
                        colorTo = context.getResources().getColor(R.color.blue_);
                    }else {
                        if (color_task_title == ContextCompat.getColor(context, R.color.blue)){
                            colorFrom = context.getResources().getColor(R.color.black_overlay);
                            colorTo = context.getResources().getColor(R.color.blue_);
                        }else if (color_task_title == ContextCompat.getColor(context, R.color.white_)){
                            colorFrom = context.getResources().getColor(R.color.black_overlay);
                            colorTo = context.getResources().getColor(R.color.dark);
                        }
                    }

                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(2500); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            holder.task_layout.setBackgroundColor((int) animator.getAnimatedValue());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout sample = new LinearLayout(new androidx.appcompat.view.ContextThemeWrapper(context, R.style.Theme_ScheduleTime_LinearLayout_ListItem_Clickable));

                                    holder.task_layout.setBackground(sample.getBackground());
                                    Drawable background = holder.task_layout.getBackground();

                                    if (background instanceof ShapeDrawable) {
                                        // cast to 'ShapeDrawable'
                                        ShapeDrawable shapeDrawable = (ShapeDrawable) background;
                                        shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, R.color.blue_));
                                    } else if (background instanceof GradientDrawable) {
                                        // cast to 'GradientDrawable'
                                        GradientDrawable gradientDrawable = (GradientDrawable) background;
                                        gradientDrawable.setColor(ContextCompat.getColor(context, R.color.blue_));
                                    } else if (background instanceof ColorDrawable) {
                                        // alpha value may need to be set again after this call
                                        ColorDrawable colorDrawable = (ColorDrawable) background;
                                        colorDrawable.setColor(ContextCompat.getColor(context, R.color.blue_));
                                    }
                                }
                            }, 2500);
                        }

                    });
                    colorAnimation.start();

                    TASK_NEW_ID = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int color = R.color.blue;
        if (task.getTask_priority().equals("low")) {
            color = R.color.low;
        }
        if (task.getTask_priority().equals("medium")) {
            color = R.color.medium;
        }
        if (task.getTask_priority().equals("high")) {
            color = R.color.high;
        }
        if (task.getTask_priority().equals("default")) {
            color = R.color.default_;
        }
        //Task color
        Drawable background = holder.task_color_view.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(context, color));
        }

        holder.task_title.setText(task.getTask_title());

        try {
            Calendar calendar_date_time = Calendar.getInstance();
            calendar_date_time.set(0, 0, 0, Integer.parseInt(task.getTask_time().substring(0, 2)), Integer.parseInt(task.getTask_time().substring(Math.max(task.getTask_time().length() - 2, 0))));

            SimpleDateFormat KK = new SimpleDateFormat("KK");
            SimpleDateFormat HH = new SimpleDateFormat("HH");
            SimpleDateFormat mm = new SimpleDateFormat("mm");
            SimpleDateFormat aa = new SimpleDateFormat("aa");

            SharedPreferences pref = context.getSharedPreferences("language", MODE_PRIVATE);
            String lang = pref.getString("lang", "");

            if (!lang.equals("")) {
                if (lang.equals("en")) {
                    Locale en = new Locale("en");
                    KK = new SimpleDateFormat("KK", en);
                    HH = new SimpleDateFormat("HH", en);
                    mm = new SimpleDateFormat("mm", en);
                    aa = new SimpleDateFormat("aa", en);
                } else if (lang.equals("fr")) {
                    Locale fr = new Locale("fr");
                    KK = new SimpleDateFormat("KK", fr);
                    HH = new SimpleDateFormat("HH", fr);
                    mm = new SimpleDateFormat("mm", fr);
                    aa = new SimpleDateFormat("aa", fr);
                } else if (lang.equals("ar")) {
                    Locale ar = new Locale("ar");
                    KK = new SimpleDateFormat("KK", ar);
                    HH = new SimpleDateFormat("HH", ar);
                    mm = new SimpleDateFormat("mm", ar);
                    aa = new SimpleDateFormat("aa", ar);
                }
            } else {
                String lang_ = Locale.getDefault().getLanguage();
                if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")) {
                    //Toast.makeText(this, getResources().getString(R.string.this_language_is_not_currently_available), Toast.LENGTH_LONG).show();
                }else {
                    Locale lng = Locale.getDefault();
                    KK = new SimpleDateFormat("KK", lng);
                    HH = new SimpleDateFormat("HH", lng);
                    mm = new SimpleDateFormat("mm", lng);
                    aa = new SimpleDateFormat("aa", lng);
                }
            }
            if (Integer.parseInt(task.getTask_time().substring(0, 2)) == 12) {
                holder.task_time.setText(String.valueOf(HH.format(calendar_date_time.getTime())) + ":" + String.valueOf(mm.format(calendar_date_time.getTime())) + " " + String.valueOf(aa.format(calendar_date_time.getTime())));
            } else {
                holder.task_time.setText(String.valueOf(KK.format(calendar_date_time.getTime())) + ":" + String.valueOf(mm.format(calendar_date_time.getTime())) + " " + String.valueOf(aa.format(calendar_date_time.getTime())));
            }

        } catch (Exception e) {
            holder.task_time.setText(R.string.error_in_time);
            e.printStackTrace();
        }


        myDB_category = new MyDatabaseHelper_category(context);


        if (!task.getTask_category().equals("[]")) {
            holder.task_category_layout.setVisibility(View.VISIBLE);
            category_ids = Arrays.asList(removeLastString(task.getTask_category().substring(1)).split(", "));

            mDots_category = new TextView[category_ids.size()];
            holder.task_category_layout.removeAllViews();

            for (int i = 0; i < mDots_category.length; i++) {
                //The Unicode and HTML Entities of Bullet Point is (&#8226;) you find it here https://unicode-table.com/fr/html-entities/

                Cursor cursor = myDB_category.readData(category_ids.get(i));
                while (cursor.moveToNext()) {
                    category_id = (cursor.getString(0));
                    category_name = (cursor.getString(1));
                    category_color = (cursor.getString(2));
                    category_deleted = (cursor.getString(3));
                }
                int color_ = Integer.parseInt(category_color);
                mDots_category[i] = new TextView(context);
                mDots_category[i].setText(Html.fromHtml("&#8226;"));
                mDots_category[i].setTextSize(56);
                mDots_category[i].setTextColor(context.getResources().getColor(color_));

                holder.task_category_layout.addView(mDots_category[i]);

            }
        } else {
            holder.task_category_layout.setVisibility(View.GONE);
        }

        holder.task_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogDetails dialogDetails = new DialogDetails((Activity) context);
                dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialogDetails.getDetails_title().setText(task.getTask_title());
                dialogDetails.getDetails_description().setText(task.getTask_description());
                if (task.getTask_priority().equals("default")) {
                    dialogDetails.getDetails_priority().setText(R.string.default_);
                    changeColorBtn(dialogDetails.getDetails_priority(), R.color.default_);
                } else if (task.getTask_priority().equals("low")) {
                    dialogDetails.getDetails_priority().setText(R.string.low);
                    changeColorBtn(dialogDetails.getDetails_priority(), R.color.low);
                } else if (task.getTask_priority().equals("medium")) {
                    dialogDetails.getDetails_priority().setText(R.string.medium);
                    changeColorBtn(dialogDetails.getDetails_priority(), R.color.medium);
                } else if (task.getTask_priority().equals("high")) {
                    dialogDetails.getDetails_priority().setText(R.string.high);
                    changeColorBtn(dialogDetails.getDetails_priority(), R.color.high);
                }

                storeDataCategoryInArraysAndLayout(dialogDetails.getDetails_category_layout_(), task.getTask_category());

                if (task.getTask_reminder().equals("0")) {
                    dialogDetails.getDetails_reminder().setText(R.string.never);
                }
                if (task.getTask_reminder().equals("1")) {
                    dialogDetails.getDetails_reminder().setText("1 minute");
                }
                if (task.getTask_reminder().equals("2")) {
                    dialogDetails.getDetails_reminder().setText("2 minute");
                }
                if (task.getTask_reminder().equals("3")) {
                    dialogDetails.getDetails_reminder().setText("3 minute");
                }
                if (task.getTask_reminder().equals("5")) {
                    dialogDetails.getDetails_reminder().setText("5 minute");
                }
                if (task.getTask_reminder().equals("10")) {
                    dialogDetails.getDetails_reminder().setText("10 minute");
                }
                if (task.getTask_reminder().equals("15")) {
                    dialogDetails.getDetails_reminder().setText("15 minute");
                }
                if (task.getTask_reminder().equals("20")) {
                    dialogDetails.getDetails_reminder().setText("20 minute");
                }
                if (task.getTask_reminder().equals("25")) {
                    dialogDetails.getDetails_reminder().setText("25 minute");
                }
                if (task.getTask_reminder().equals("30")) {
                    dialogDetails.getDetails_reminder().setText("30 minute");
                }
                if (task.getTask_reminder().equals("45")) {
                    dialogDetails.getDetails_reminder().setText("45 minute");
                }
                if (task.getTask_reminder().equals("60")) {
                    dialogDetails.getDetails_reminder().setText("1 hour");
                }
                if (task.getTask_reminder().equals("120")) {
                    dialogDetails.getDetails_reminder().setText("2 hours");
                }
                if (task.getTask_reminder().equals("180")) {
                    dialogDetails.getDetails_reminder().setText("3 hours");
                }
                if (task.getTask_reminder().equals("300")) {
                    dialogDetails.getDetails_reminder().setText("5 hours");
                }
                if (task.getTask_reminder().equals("720")) {
                    dialogDetails.getDetails_reminder().setText("12 hours");
                }
                if (task.getTask_reminder().equals("1440")) {
                    dialogDetails.getDetails_reminder().setText("1 day");
                }
                if (task.getTask_reminder().equals("2880")) {
                    dialogDetails.getDetails_reminder().setText("2 days");
                }
                if (task.getTask_reminder().equals("4320")) {
                    dialogDetails.getDetails_reminder().setText("3 days");
                }
                if (task.getTask_reminder().equals("7200")) {
                    dialogDetails.getDetails_reminder().setText("5 days");
                }
                if (task.getTask_reminder().equals("10080")) {
                    dialogDetails.getDetails_reminder().setText("1 week");
                }
                if (task.getTask_reminder().equals("20160")) {
                    dialogDetails.getDetails_reminder().setText("2 weeks");
                }
                if (task.getTask_reminder().equals("30240")) {
                    dialogDetails.getDetails_reminder().setText("3 weeks");
                }
                if (task.getTask_reminder().equals("40320")) {
                    dialogDetails.getDetails_reminder().setText("4 weeks");
                }

                dialogDetails.getDetails_edit().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Edit Your Task in EditTaskActivity.class
                        Intent intent = new Intent(context, EditTaskActivity.class);
                        intent.putExtra("id", task.getTask_id());
                        ((Activity) context).startActivityForResult(intent, 10);
                        dialogDetails.dismiss();
                    }
                });
                dialogDetails.show();
                dialogDetails.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            }
        });
        holder.task_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                BottomDialogOther bottomDialogOther = new BottomDialogOther((Activity) context);
                //bottomDialogDelete.getSheet_dialog_title().setText("Delete \"Lorem\" ?");
                bottomDialogOther.getOther_dialog_edit().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Edit Your Task in EditTaskActivity.class
                        Intent intent = new Intent(context, EditTaskActivity.class);
                        intent.putExtra("id", task.getTask_id());
                        ((Activity) context).startActivityForResult(intent, 10);
                        bottomDialogOther.dismiss();
                    }
                });
                bottomDialogOther.getOther_dialog_duplicate().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        currentTime = Calendar.getInstance().getTime();

                        DialogNewTask dialogNewTask = new DialogNewTask((Activity) context);
                        dialogNewTask.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        dialogNewTask.getEditTextTitle().setText(task.getTask_title());
                        dialogNewTask.getEditTextDescription().setText(task.getTask_description());
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
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainerView, new ListFragment())
                                            .commit();
                                    scrollToNewTask = true;
                                } else if (fOpen == 2) {
                                    //restart calendarFragment and scroll to new task
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
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

                        bottomDialogOther.dismiss();
                    }
                });
                bottomDialogOther.getOther_dialog_delete().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomDialogOther.dismiss();

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                        title.setText(context.getResources().getString(R.string.delete_this_task) + " :\n\"" + String.valueOf(task.getTask_title()) + "\" ?");
                        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                                myDB.deleteOneRow(task.getTask_id());
                                holder.task_layout.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                    }
                });
                bottomDialogOther.getOther_dialog_cancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, context.getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                        bottomDialogOther.dismiss();
                    }
                });
                bottomDialogOther.build();
                bottomDialogOther.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomDialogOther.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bottomDialogOther.getWindow().setWindowAnimations(R.style.SheetDialogAnimation);
                bottomDialogOther.getWindow().setGravity(Gravity.BOTTOM);
                return true;//true for show just the long click listener
            }
        });

    }

    private String removeLastString(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class RecyclerViewTasksHolder extends RecyclerView.ViewHolder {

        private View task_color_view;
        private LinearLayout task_layout, task_category_layout;
        private TextView task_title, task_time;

        public RecyclerViewTasksHolder(@NonNull View itemView) {
            super(itemView);

            task_color_view = itemView.findViewById(R.id.task_color_view);
            task_layout = itemView.findViewById(R.id.task_layout);
            task_title = itemView.findViewById(R.id.task_title);
            task_time = itemView.findViewById(R.id.task_time);
            task_category_layout = itemView.findViewById(R.id.task_category_layout);

        }
    }

    private void storeDataCategoryInArraysAndLayout(LinearLayout category_layout_, String task_category) {
        if (!task_category.equals("[]")) {
            category_ids = Arrays.asList(removeLastString(task_category.substring(1)).split(", "));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 108);
            params.setMarginEnd(8);

            category_btn = new Button[category_ids.size()];//category_ids.size
            category_layout_.removeAllViews();

            for (int i = 0; i < category_btn.length; i++) {

                Cursor cursor = myDB_category.readData(category_ids.get(i));
                while (cursor.moveToNext()) {
                    category_id = (cursor.getString(0));
                    category_name = (cursor.getString(1));
                    category_color = (cursor.getString(2));
                    category_deleted = (cursor.getString(3));
                }

                category_btn[i] = new Button(context);
                category_btn[i].setText(category_name.toString());
                category_btn[i].setTextSize(12);
                category_btn[i].setAllCaps(false);
                category_btn[i].setTextColor(ContextCompat.getColor(context, R.color.blue));
                category_btn[i].setBackgroundResource(R.drawable.custom_button_priority);
                changeColorBtn(category_btn[i], Integer.parseInt(category_color));
                category_btn[i].setLayoutParams(params);

                category_layout_.addView(category_btn[i]);
            }
        } else {
            category_layout_.setVisibility(View.GONE);
        }
    }

    public void changeColorBtn(Button btn, int color) {
        Drawable background = btn.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(context, color));
        }
    }
}
