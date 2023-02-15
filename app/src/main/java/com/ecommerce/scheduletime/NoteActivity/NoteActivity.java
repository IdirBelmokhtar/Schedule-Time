package com.ecommerce.scheduletime.NoteActivity;

import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.searchingNotes;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.isSelectedModeNote;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.selectAllNotes;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.selectedItemNote;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.showItemMenu;
import static com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter.unSelectAllNotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.ecommerce.scheduletime.Adapter.RecyclerViewNotesAdapter;
import com.ecommerce.scheduletime.Model.Notes;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;
import com.ecommerce.scheduletime.SQLite.NewNotes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    private RecyclerView recyclerViewNotes;
    List<Notes> notes = new ArrayList<>();

    MyDatabaseHelper_notes myDB;
    ArrayList<String> note_id, note_id_, note_title, note_description, note_time;
    RecyclerViewNotesAdapter recyclerViewNotesAdapter;
    public static boolean filter = true;

    SearchView searchView;
    public static LinearLayout note_add;
    public static ImageView addNote;
    public static LinearLayout deleteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Open dialog presentation first time.
        if (getSharedPreferences("NOTE", MODE_PRIVATE).getString("note", "").equals("")) {
            final Dialog dialog = new Dialog(NoteActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_note_presentation);
            dialog.findViewById(R.id.dialog_note_presentation_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            SharedPreferences.Editor editor = getSharedPreferences("NOTE", MODE_PRIVATE).edit();
            editor.putString("note", "done");
            editor.apply();
        }

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        toolbar = findViewById(R.id.toolbar_note);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_note:
                        // Search note.
                        searchView = (SearchView) item.getActionView();
                        searchView.setQueryHint(getResources().getString(R.string.search));
                        searchView.clearFocus();
                        searchView.setIconified(false);
                        note_add.setVisibility(View.GONE);
                        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {
                                    // searchView expanded
                                    note_add.setVisibility(View.GONE);
                                } else {
                                    // searchView not expanded
                                    note_add.setVisibility(View.VISIBLE);
                                    //item.setIcon()
                                }
                            }
                        });
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                searchingNotes = s;
                                List<Notes> searchList = new ArrayList<>();
                                for (Notes viewSearch : notes) {
                                    if (viewSearch.getTitle().toLowerCase().contains(s.toLowerCase())) {
                                        searchList.add(viewSearch);
                                    } else if (viewSearch.getDescription().toLowerCase().contains(s.toLowerCase())) {
                                        searchList.add(viewSearch);
                                    }
                                }
                                recyclerViewNotesAdapter.setSearchList(NoteActivity.this, searchList);
                                runLayoutAnimation();
                                return true;
                            }
                        });
                        break;
                    case R.id.filter_note:
                        // Changing the layout manager followed by applying the animation + icon

                        if (filter) {
                            recyclerViewNotes.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
                            item.setIcon(R.drawable.icons8_deviation);
                        } else {
                            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                            item.setIcon(R.drawable.ic_baseline_filter_list_24);
                        }
                        runLayoutAnimation();
                        filter = !filter;
                        break;
                    case R.id.check_all_note:
                        selectAllNotes(recyclerViewNotesAdapter);
                        item.setVisible(false);

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

        // Notes. (RecyclerView)

        myDB = new MyDatabaseHelper_notes(NoteActivity.this);
        note_id = new ArrayList<>();
        note_id_ = new ArrayList<>();
        note_title = new ArrayList<>();
        note_description = new ArrayList<>();
        note_time = new ArrayList<>();

        storeDataInArrays();

        for (int i = 0; i < note_id.size(); i++) {
            notes.add(new Notes(note_id.get(i), note_id_.get(i), note_title.get(i), note_description.get(i), note_time.get(i)));
        }
        recyclerViewNotesAdapter = new RecyclerViewNotesAdapter(NoteActivity.this, NoteActivity.this, notes);
        recyclerViewNotes.setAdapter(recyclerViewNotesAdapter);
        if (!filter) {
            // Changing the layout manager followed by applying the animation
            recyclerViewNotes.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
            runLayoutAnimation();
        } else {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            runLayoutAnimation();
        }

        addNote = findViewById(R.id.addNote);
        deleteNote = findViewById(R.id.deleteNote);
        // Add note.
        note_add = findViewById(R.id.note_add);
        note_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSelectedModeNote) {
                    addNote();
                } else {
                    deleteNotes();
                }
            }
        });
    }

    private void deleteNotes() {
        final Dialog dialog = new Dialog(NoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

        title.setText(R.string.delete_this_notes);
        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper_notes myDB = new MyDatabaseHelper_notes(NoteActivity.this);

                for (int i = 0; i < selectedItemNote.size(); i++) {
                    myDB.deleteOneRow(String.valueOf(selectedItemNote.get(i).getId()));
                }

                searchingNotes = "";
                showItemMenu();
                unSelectAllNotes(recyclerViewNotesAdapter);
                recyclerViewNotesAdapter.notifyDataSetChanged();
                recreate();

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addNote() {
        Intent intent = new Intent(NoteActivity.this, EditNoteActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", "");
        intent.putExtra("id_", "");
        intent.putExtra("title", "");
        intent.putExtra("description", "");
        intent.putExtra("time", "");
        intent.putExtra("duplicate", "");
        ((Activity) NoteActivity.this).startActivityForResult(intent, 10);
        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private void runLayoutAnimation() {
        final Context context = recyclerViewNotes.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerViewNotes.setLayoutAnimation(controller);
        recyclerViewNotes.getAdapter().notifyDataSetChanged();
        recyclerViewNotes.scheduleLayoutAnimation();
    }

    void storeDataInArrays() {
        note_id.clear();
        note_id_.clear();
        note_title.clear();
        note_description.clear();
        note_time.clear();

        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            findViewById(R.id.no_data_edit_note).setVisibility(View.VISIBLE);
            findViewById(R.id.no_data_edit_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNote();
                }
            });
        } else {
            findViewById(R.id.no_data_edit_note).setVisibility(View.GONE);

            while (cursor.moveToNext()) {
                note_id.add(cursor.getString(0));
                note_id_.add(cursor.getString(1));
                note_title.add(cursor.getString(2));
                note_description.add(cursor.getString(3));
                note_time.add(cursor.getString(4));
            }
        }

        sortByDateAndTime();
    }

    private void sortByDateAndTime() {

        List<Long> tasksTime = new ArrayList<>();
        for (int i = 0; i < note_id.size(); i++) {
            Calendar date = Calendar.getInstance();
            List<String> time = Arrays.asList(note_time.get(i).split("-"));
            date.set(Integer.parseInt(time.get(0)), Integer.parseInt(time.get(1)), Integer.parseInt(time.get(2)), Integer.parseInt(time.get(3)), Integer.parseInt(time.get(4)));

            tasksTime.add(date.getTimeInMillis());
        }

        for (int i = 0; i < note_id.size() - 1; i++) {
            int index = i;

            for (int j = i + 1; j < note_id.size(); j++) {
                if (tasksTime.get(j) > tasksTime.get(index)) {
                    index = j;
                }
            }

            Long min = tasksTime.get(index);
            tasksTime.set(index, tasksTime.get(i));
            tasksTime.set(i, min);

            String id = note_id.get(index);
            String id_ = note_id_.get(index);
            String date = note_title.get(index);
            String title = note_description.get(index);
            String description = note_time.get(index);

            note_id.set(index, note_id.get(i));
            note_id_.set(index, note_id_.get(i));
            note_title.set(index, note_title.get(i));
            note_description.set(index, note_description.get(i));
            note_time.set(index, note_time.get(i));

            note_id.set(i, id);
            note_id_.set(i, id_);
            note_title.set(i, date);
            note_description.set(i, title);
            note_time.set(i, description);

        }
    }

    @Override
    public void onBackPressed() {
        if (!searchingNotes.equals("")) {
            note_add.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchingNotes = "";
        } else {
            if (isSelectedModeNote) {
                searchingNotes = "";
                showItemMenu();
                unSelectAllNotes(recyclerViewNotesAdapter);
            } else {
                super.onBackPressed();
            }

        }
    }
}