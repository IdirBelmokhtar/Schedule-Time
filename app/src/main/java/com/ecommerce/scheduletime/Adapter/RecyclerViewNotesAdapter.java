package com.ecommerce.scheduletime.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.scheduletime.Model.Notes;
import com.ecommerce.scheduletime.NoteActivity.EditNoteActivity;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class RecyclerViewNotesAdapter extends RecyclerView.Adapter<RecyclerViewNotesAdapter.RecyclerViewNotesHolder> {

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
        holder.note_title.setText(note.getTitle());
        if (note.getDescription().length() > 120){
            holder.note_description.setText(note.getDescription().substring(0, 120) + "...");
        }else {
            holder.note_description.setText(note.getDescription());
        }
        holder.note_time.setText(getTime(note.getTime()));

        holder.note_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditNoteActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", id);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("description", note.getDescription());
                intent.putExtra("time", note.getTime());
                ((Activity) context).startActivityForResult(intent, 10);
                //((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                ((Activity) context).finish();
            }
        });
        holder.note_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Select layer.
                return true;
            }
        });
        int finalPosition = position;
        holder.note_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")){
                //Toast.makeText(getContext(), getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable), Toast.LENGTH_LONG).show();
            }else {
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

        public RecyclerViewNotesHolder(@NonNull View itemView) {
            super(itemView);

            note_layout = itemView.findViewById(R.id.my_recycler_view_notes_layout);
            note_description = itemView.findViewById(R.id.my_recycler_view_notes_description);
            note_title = itemView.findViewById(R.id.my_recycler_view_notes_title);
            note_time = itemView.findViewById(R.id.my_recycler_view_notes_time);
            note_delete = itemView.findViewById(R.id.layout_edit_note_delete);

        }
    }
}