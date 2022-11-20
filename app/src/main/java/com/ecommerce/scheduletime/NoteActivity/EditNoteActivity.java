package com.ecommerce.scheduletime.NoteActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView edit_note_title_toolbar;

    EditText title, description;
    TextView time;
    String time_;
    String id = "";

    MyDatabaseHelper_notes myDB = new MyDatabaseHelper_notes(EditNoteActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        id = getIntent().getStringExtra("id");

        edit_note_title_toolbar = findViewById(R.id.edit_note_title_toolbar);
        title = findViewById(R.id.edit_note_title);
        description = findViewById(R.id.edit_note_description);
        toolbar = findViewById(R.id.toolbar_editNote);
        time = findViewById(R.id.edit_note_time);

        Calendar calendar = Calendar.getInstance();

        if (!id.equals("")){
            edit_note_title_toolbar.setText(getIntent().getStringExtra("title"));
            title.setText(getIntent().getStringExtra("title"));
            description.setText(getIntent().getStringExtra("description"));
            time_ = getIntent().getStringExtra("time");
        }else {
            time_ =       String.valueOf(calendar.get(Calendar.YEAR)) + // calendar.getTime().getYear() return year = 122 not 2022!
                    "-" + String.valueOf(calendar.getTime().getMonth()) +
                    "-" + String.valueOf(calendar.getTime().getDate()) +
                    "-" + String.valueOf(calendar.getTime().getHours()) +
                    "-" + String.valueOf(calendar.getTime().getMinutes());
        }
        time.setText(String.valueOf(getTime(time_)));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_edit_note:
                        if (!id.equals("")){
                            final Dialog dialog = new Dialog(EditNoteActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                            TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                            LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                            LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                            title.setText("Delete this note ?");
                            sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    myDB.deleteOneRow(id);
                                    onBackPressed();
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                            dialog.getWindow().setGravity(Gravity.BOTTOM);
                        }else {
                            onBackPressed();
                        }
                        break;
                    case R.id.save_edit_note:
                        time_ =       String.valueOf(calendar.get(Calendar.YEAR)) + // calendar.getTime().getYear() return year = 122 not 2022!
                                "-" + String.valueOf(calendar.getTime().getMonth()) +
                                "-" + String.valueOf(calendar.getTime().getDate()) +
                                "-" + String.valueOf(calendar.getTime().getHours()) +
                                "-" + String.valueOf(calendar.getTime().getMinutes());
                        if (id.equals("")) {
                            myDB.addBook(title.getText().toString().trim(),
                                    description.getText().toString().trim(),
                                    time_);
                        }else {
                            myDB.updateData(id,
                                    title.getText().toString().trim(),
                                    description.getText().toString().trim(),
                                    time_);
                        }
                        onBackPressed();
                        break;
                }
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String getTime(String time_) {
        String date, time;
        List<String> s = Arrays.asList(time_.split("-"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(s.get(0)), Integer.parseInt(s.get(1)), Integer.parseInt(s.get(2)), Integer.parseInt(s.get(3)), Integer.parseInt(s.get(4)));

        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        date = (String) DateFormat.format("MMM", calendar) + " " + (String) DateFormat.format("dd", calendar) + ", " + (String) DateFormat.format("yyyy", calendar);
        if (Integer.parseInt(s.get(3)) == 12) {
            time = String.valueOf(HH.format(calendar.getTime())) + ":" + String.valueOf(mm.format(calendar.getTime())) + " " + String.valueOf(aa.format(calendar.getTime()));
        } else {
            time = String.valueOf(KK.format(calendar.getTime())) + ":" + String.valueOf(mm.format(calendar.getTime())) + " " + String.valueOf(aa.format(calendar.getTime()));
        }
        return date + " - " + time;
    }

    @Override
    public void onBackPressed() {
        //overridePendingTransition(0, 0);
        startActivity(new Intent(this, NoteActivity.class));
        //overridePendingTransition(0, 0);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        super.onBackPressed();
    }
}