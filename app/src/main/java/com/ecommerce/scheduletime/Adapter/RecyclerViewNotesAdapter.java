package com.ecommerce.scheduletime.Adapter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.ecommerce.scheduletime.HomeActivity.MainActivity.currentTime;
import static com.ecommerce.scheduletime.NoteActivity.NoteActivity.addNote;
import static com.ecommerce.scheduletime.NoteActivity.NoteActivity.deleteNote;
import static com.ecommerce.scheduletime.NoteActivity.NoteActivity.toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.scheduletime.Dialog.BottomDialogOther_Note;
import com.ecommerce.scheduletime.Dialog.DialogNewTask;
import com.ecommerce.scheduletime.Model.Notes;
import com.ecommerce.scheduletime.NoteActivity.EditNoteActivity;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecyclerViewNotesAdapter extends RecyclerView.Adapter<RecyclerViewNotesAdapter.RecyclerViewNotesHolder> {

    public static String searchingNotes = "";
    public static boolean isSelectedModeNote = false;
    public static List<Notes> selectedItemNote = new ArrayList<>();

    int color_note_title;

    private List<Notes> notes;
    private Context context;
    Activity activity;

    public RecyclerViewNotesAdapter(Context context, Activity activity, List<Notes> notes) {
        this.notes = notes;
        this.context = context;
        this.activity = activity;
    }

    public void setSearchList(Context context, List<Notes> searchList) {
        this.notes = searchList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewNotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recycler_view_notes, parent, false);
        return new RecyclerViewNotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewNotesHolder holder, int position) {
        Notes note = notes.get(position);

        String id = note.getId();

        // Surbrillance les caractéres de recherche.
        String noteTitle = note.getTitle();
        String noteDescription = note.getDescription();
        String highlight = searchingNotes;

        if (noteTitle.contains(highlight)) {
            int start = noteTitle.indexOf(highlight);
            int end = start + highlight.length();

            SpannableString spannable = new SpannableString(noteTitle);
            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.note_title.setText(spannable);
        } else {
            holder.note_title.setText(noteTitle);
        }

        if (noteDescription.contains(highlight)) {
            int start = noteDescription.indexOf(highlight);
            int end = start + highlight.length();

            SpannableString spannable = new SpannableString(noteDescription);
            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (noteDescription.length() > 120) {
                CharSequence displayText = spannable.subSequence(0, Math.min(spannable.length(), 120));
                if (spannable.length() > 120) {
                    displayText = TextUtils.concat(displayText, "...");
                }
                holder.note_description.setText(displayText);
            } else {
                holder.note_description.setText(spannable);
            }
        } else {
            if (noteDescription.length() > 120) {
                holder.note_description.setText(noteDescription.substring(0, 120) + "...");
            } else {
                holder.note_description.setText(noteDescription);
            }
        }

        holder.note_time.setText(getTime(note.getTime()));

        int finalPosition = position;
        //Dark Mode
        SharedPreferences preferences_ = context.getSharedPreferences("dark_mode", MODE_PRIVATE);
        String state = preferences_.getString("state", "");

        color_note_title = holder.note_title.getCurrentTextColor();

        // Selectionner tout.
        if (isSelectedModeNote){
            if (selectedItemNote.contains(notes.get(finalPosition))) {
                unSelectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);
            } else {
                selectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);
            }
            if (selectedItemNote.size() == 0) {
                addNote.setVisibility(View.VISIBLE);
                deleteNote.setVisibility(View.GONE);
                isSelectedModeNote = false;
                showItemMenu();
            }
        }
        // Désélectionner tout
        else {
            unSelectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);
        }

        holder.note_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelectedModeNote){
                    if (selectedItemNote.contains(notes.get(finalPosition))) {
                        unSelectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);
                    } else {
                        selectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);
                    }
                    if (selectedItemNote.size() == 0) {
                        addNote.setVisibility(View.VISIBLE);
                        deleteNote.setVisibility(View.GONE);
                        isSelectedModeNote = false;
                        showItemMenu();
                    }
                }else {
                    searchingNotes = "";
                    Intent intent = new Intent(context, EditNoteActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("title", note.getTitle());
                    intent.putExtra("description", note.getDescription());
                    intent.putExtra("time", note.getTime());
                    intent.putExtra("duplicate", "");
                    ((Activity) context).startActivityForResult(intent, 10);
                    //((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    ((Activity) context).finish();
                }
            }
        });

        holder.note_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isSelectedModeNote) {

                    BottomDialogOther_Note bottomDialogOther_note = new BottomDialogOther_Note((Activity) context);
                    bottomDialogOther_note.getOther_dialog_edit_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchingNotes = "";
                            Intent intent = new Intent(context, EditNoteActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("id", id);
                            intent.putExtra("title", note.getTitle());
                            intent.putExtra("description", note.getDescription());
                            intent.putExtra("time", note.getTime());
                            intent.putExtra("duplicate", "");
                            ((Activity) context).startActivityForResult(intent, 10);
                            //((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            ((Activity) context).finish();
                            bottomDialogOther_note.dismiss();
                        }
                    });
                    bottomDialogOther_note.getOther_dialog_select_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addNote.setVisibility(View.GONE);
                            deleteNote.setVisibility(View.VISIBLE);
                            isSelectedModeNote = true;
                            hideItemMenu();
                            selectNote(context, state, holder.note_layout, holder.checkBoxSelection, finalPosition);

                            //notifyItemChanged(finalPosition);

                            bottomDialogOther_note.dismiss();
                        }
                    });
                    bottomDialogOther_note.getOther_dialog_create_task_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            currentTime = Calendar.getInstance().getTime();

                            DialogNewTask dialogNewTask = new DialogNewTask((Activity) context);
                            dialogNewTask.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            dialogNewTask.getEditTextTitle().setText(note.getTitle());
                            dialogNewTask.getEditTextDescription().setText(note.getDescription());
                            dialogNewTask.getNew_saveBtn().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                                    myDB.addSchedule(dialogNewTask.getDate(), dialogNewTask.getTitle(), dialogNewTask.getDescription(),
                                            dialogNewTask.getPriority(), dialogNewTask.getCategory(), dialogNewTask.getTime(), "no",
                                            dialogNewTask.getReminder());

                                    dialogNewTask.dismiss();

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("key", "8");
                                    activity.setResult(RESULT_OK, resultIntent);
                                    activity.onBackPressed();

                                    Toast.makeText(context, context.getResources().getString(R.string.create_task_successful), Toast.LENGTH_SHORT).show();
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

                            bottomDialogOther_note.dismiss();
                        }
                    });
                    bottomDialogOther_note.getOther_dialog_duplicate_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchingNotes = "";
                            Intent intent = new Intent(context, EditNoteActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("id", "");
                            intent.putExtra("duplicate", "yes");
                            intent.putExtra("duplicate_title", note.getTitle());
                            intent.putExtra("duplicate_description", note.getDescription());
                            ((Activity) context).startActivityForResult(intent, 10);
                            //((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            ((Activity) context).finish();
                        }
                    });
                    bottomDialogOther_note.getOther_dialog_delete_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomDialogOther_note.dismiss();

                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                            TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                            LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                            LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                            title.setText(R.string.delete_this_note);
                            sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MyDatabaseHelper_notes myDB = new MyDatabaseHelper_notes(context);
                                    myDB.deleteOneRow(id);

                                    try {
                                        notes.remove(finalPosition);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    notifyItemRemoved(finalPosition);
                                    notifyItemRangeChanged(finalPosition, getItemCount() - 1);

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
                    bottomDialogOther_note.getOther_dialog_cancel_note().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, context.getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                            bottomDialogOther_note.dismiss();
                        }
                    });
                    bottomDialogOther_note.build();
                    bottomDialogOther_note.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    bottomDialogOther_note.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomDialogOther_note.getWindow().setWindowAnimations(R.style.SheetDialogAnimation);
                    bottomDialogOther_note.getWindow().setGravity(Gravity.BOTTOM);

                }

                // Select layer.
                return true;
            }
        });
        holder.note_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSelectedModeNote){

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                    TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                    LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                    LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                    title.setText(R.string.delete_this_note);
                    sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyDatabaseHelper_notes myDB = new MyDatabaseHelper_notes(context);
                            myDB.deleteOneRow(id);

                            try {
                                notes.remove(finalPosition);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            notifyItemRemoved(finalPosition);
                            notifyItemRangeChanged(finalPosition, getItemCount() - 1);

                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                }
            }
        });

    }

    public static void showItemMenu() {
        Menu menu = toolbar.getMenu();
        MenuItem menuItem1 = menu.findItem(R.id.search_note);
        MenuItem menuItem2 = menu.findItem(R.id.filter_note);
        MenuItem menuItem3 = menu.findItem(R.id.check_all_note);
        menuItem1.setVisible(true);
        menuItem2.setVisible(true);
        menuItem3.setVisible(false);
        //UnselectAllNote
    }

    public static void hideItemMenu() {
        Menu menu = toolbar.getMenu();
        MenuItem menuItem1 = menu.findItem(R.id.search_note);
        MenuItem menuItem2 = menu.findItem(R.id.filter_note);
        MenuItem menuItem3 = menu.findItem(R.id.check_all_note);
        menuItem1.setVisible(false);
        menuItem2.setVisible(false);
        menuItem3.setVisible(true);
    }

    public static void unSelectAllNotes(RecyclerViewNotesAdapter recyclerViewNotesAdapter){
        addNote.setVisibility(View.VISIBLE);
        deleteNote.setVisibility(View.GONE);
        isSelectedModeNote = false;
        selectedItemNote.clear();
        recyclerViewNotesAdapter.notifyDataSetChanged();
    }

    public static void selectAllNotes(RecyclerViewNotesAdapter recyclerViewNotesAdapter){
        addNote.setVisibility(View.GONE);
        deleteNote.setVisibility(View.VISIBLE);
        isSelectedModeNote = true;
        selectedItemNote.clear();
        recyclerViewNotesAdapter.notifyDataSetChanged();
    }

    private void unSelectNote(Context context, String state, LinearLayout note_layout, ImageView checkBoxSelection, int finalPosition) {
        selectedItemNote.remove(notes.get(finalPosition));

        Drawable drawableDark = context.getDrawable(R.drawable.ripple_list_item_bg_circle_white_dark_mode);
        Drawable drawableLight = context.getDrawable(R.drawable.ripple_list_item_bg_circle_white_);
        if (state.equals("true")) {
            note_layout.setBackground(drawableDark);
            checkBoxSelection.setVisibility(View.GONE);
        } else if (state.equals("false")) {
            note_layout.setBackground(drawableLight);
            checkBoxSelection.setVisibility(View.GONE);
        } else {
            if (color_note_title == ContextCompat.getColor(context, R.color.blue)){
                note_layout.setBackground(drawableLight);
                checkBoxSelection.setVisibility(View.GONE);
            }else if (color_note_title == ContextCompat.getColor(context, R.color.white_)){
                note_layout.setBackground(drawableDark);
                checkBoxSelection.setVisibility(View.GONE);
            }
        }
    }

    private void selectNote(Context context, String state, LinearLayout note_layout, ImageView checkBoxSelection, int finalPosition) {
        selectedItemNote.add(notes.get(finalPosition));

        Drawable drawableDarkSelection = context.getDrawable(R.drawable.ripple_list_item_bg_circle_white_dark_mode_selection);
        Drawable drawableLightSelection = context.getDrawable(R.drawable.ripple_list_item_bg_circle_white_selection);
        if (state.equals("true")) {
            note_layout.setBackground(drawableDarkSelection);
            checkBoxSelection.setVisibility(View.VISIBLE);
        } else if (state.equals("false")) {
            note_layout.setBackground(drawableLightSelection);
            checkBoxSelection.setVisibility(View.VISIBLE);
        } else {
            if (color_note_title == ContextCompat.getColor(context, R.color.blue)){
                note_layout.setBackground(drawableLightSelection);
                checkBoxSelection.setVisibility(View.VISIBLE);
            }else if (color_note_title == ContextCompat.getColor(context, R.color.white_)){
                note_layout.setBackground(drawableDarkSelection);
                checkBoxSelection.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getTime(String time_) {
        String date, time;
        List<String> s = Arrays.asList(time_.split("-"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(s.get(0)), Integer.parseInt(s.get(1)), Integer.parseInt(s.get(2)), Integer.parseInt(s.get(3)), Integer.parseInt(s.get(4)));

        SimpleDateFormat MMM = new SimpleDateFormat("MMM");
        SimpleDateFormat dd = new SimpleDateFormat("dd");
        SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        SharedPreferences pref = context.getSharedPreferences("language", MODE_PRIVATE);
        String lang = pref.getString("lang", "");

        if (!lang.equals("")) {
            if (lang.equals("en")) {
                Locale en = new Locale("en");
                MMM = new SimpleDateFormat("MMM", en);
                dd = new SimpleDateFormat("dd", en);
                yyyy = new SimpleDateFormat("yyyy", en);
                KK = new SimpleDateFormat("KK", en);
                HH = new SimpleDateFormat("HH", en);
                mm = new SimpleDateFormat("mm", en);
                aa = new SimpleDateFormat("aa", en);
            } else if (lang.equals("fr")) {
                Locale fr = new Locale("fr");
                MMM = new SimpleDateFormat("MMM", fr);
                dd = new SimpleDateFormat("dd", fr);
                yyyy = new SimpleDateFormat("yyyy", fr);
                KK = new SimpleDateFormat("KK", fr);
                HH = new SimpleDateFormat("HH", fr);
                mm = new SimpleDateFormat("mm", fr);
                aa = new SimpleDateFormat("aa", fr);
            } else if (lang.equals("ar")) {
                Locale ar = new Locale("ar");
                MMM = new SimpleDateFormat("MMM", ar);
                dd = new SimpleDateFormat("dd", ar);
                yyyy = new SimpleDateFormat("yyyy", ar);
                KK = new SimpleDateFormat("KK", ar);
                HH = new SimpleDateFormat("HH", ar);
                mm = new SimpleDateFormat("mm", ar);
                aa = new SimpleDateFormat("aa", ar);
            }
        } else {
            String lang_ = Locale.getDefault().getLanguage();

            if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")) {
                //Toast.makeText(getContext(), getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable), Toast.LENGTH_LONG).show();
            } else {
                Locale lng = Locale.getDefault();
                MMM = new SimpleDateFormat("MMM", lng);
                dd = new SimpleDateFormat("dd", lng);
                yyyy = new SimpleDateFormat("yyyy", lng);
                KK = new SimpleDateFormat("KK", lng);
                HH = new SimpleDateFormat("HH", lng);
                mm = new SimpleDateFormat("mm", lng);
                aa = new SimpleDateFormat("aa", lng);
            }
        }

        //date = (String) DateFormat.format("MMM", calendar) + " " + (String) DateFormat.format("dd", calendar) + ", " + (String) DateFormat.format("yyyy", calendar);
        date = String.valueOf(MMM.format(calendar.getTime())) + " " + String.valueOf(dd.format(calendar.getTime())) + ", " + String.valueOf(yyyy.format(calendar.getTime()));
        if (Integer.parseInt(s.get(3)) == 12) {
            time = String.valueOf(HH.format(calendar.getTime())) + ":" + String.valueOf(mm.format(calendar.getTime())) + " " + String.valueOf(aa.format(calendar.getTime()));
        } else {
            time = String.valueOf(KK.format(calendar.getTime())) + ":" + String.valueOf(mm.format(calendar.getTime())) + " " + String.valueOf(aa.format(calendar.getTime()));
        }
        return date + " - " + time;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class RecyclerViewNotesHolder extends RecyclerView.ViewHolder {

        private LinearLayout note_layout, note_delete;
        private TextView note_title, note_description, note_time;
        private ImageView checkBoxSelection;

        public RecyclerViewNotesHolder(@NonNull View itemView) {
            super(itemView);

            note_layout = itemView.findViewById(R.id.my_recycler_view_notes_layout);
            note_description = itemView.findViewById(R.id.my_recycler_view_notes_description);
            note_title = itemView.findViewById(R.id.my_recycler_view_notes_title);
            note_time = itemView.findViewById(R.id.my_recycler_view_notes_time);
            note_delete = itemView.findViewById(R.id.layout_edit_note_delete);
            checkBoxSelection = itemView.findViewById(R.id.checkBoxSelection);

        }
    }

}