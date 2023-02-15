package com.ecommerce.scheduletime.Sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.Model.Firebase.RealtimeCategory;
import com.ecommerce.scheduletime.Model.Firebase.RealtimeNotes;
import com.ecommerce.scheduletime.Model.Firebase.RealtimeTasks;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncDatabase {
    ProgressDialog progressDialog;

    Context context;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference referenceTasks;
    DatabaseReference referenceCategories;
    DatabaseReference referenceNotes;

    public SyncDatabase(@Nullable Context context, String fUserID) {
        this.context = context;
        referenceUser = referenceUser.child(fUserID);

        referenceTasks = referenceUser.child("Tasks");
        referenceCategories = referenceUser.child("Categories");
        referenceNotes = referenceUser.child("Notes");

        //setAllDataIntoRealtimeDatabase();


        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the UID exists in the database
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    if (dataSnapshot.getKey().equals(fUserID)){
                        Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
                        getAllDataSQLiteDatabase(fUserID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
        //Toast.makeText(context, "User Uid not exist in Realtime DB", Toast.LENGTH_LONG).show();
        //setAllDataIntoRealtimeDatabase();
    }

    private void getAllDataSQLiteDatabase(String fUserID) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_sync_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        progressDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);


        // Get Tasks Realtime Database into SQLite.
        ArrayList<String> task_id = new ArrayList<>();
        ArrayList<String> task_date = new ArrayList<>();
        ArrayList<String> task_title = new ArrayList<>();
        ArrayList<String> task_description = new ArrayList<>();
        ArrayList<String> task_priority = new ArrayList<>();
        ArrayList<String> task_category = new ArrayList<>();
        ArrayList<String> task_time = new ArrayList<>();
        ArrayList<String> task_done = new ArrayList<>();
        ArrayList<String> task_reminder = new ArrayList<>();

        referenceTasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RealtimeTasks task = dataSnapshot.getValue(RealtimeTasks.class);
                    task_id.add(task.get_id());
                    task_date.add(task.getTask_date());
                    task_title.add(task.getTask_title());
                    task_description.add(task.getTask_description());
                    task_priority.add(task.getTask_priority());
                    task_category.add(task.getTask_category());
                    task_time.add(task.getTask_time());
                    task_done.add(task.getTask_done());
                    task_reminder.add(task.getTask_reminder());
                }
                Toast.makeText(context, "task : " + String.valueOf(task_id.size()), Toast.LENGTH_SHORT).show();
                // Store data in local database
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                myDB.deleteAllData();
                for (int i = 0; i < task_id.size(); i++) {
                    myDB.addSchedule(task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i),
                            task_category.get(i), task_time.get(i), task_done.get(i), Integer.parseInt(task_reminder.get(i)));
                }
                // Modifie les ids pour que les donne de Database SQL sois la meme que Firebase
                for (int i = 0; i < task_id.size(); i++) {
                    //myDB.updateData(task_id.get(i), task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i), task_category.get(i), task_time.get(i), task_done.get(i), Integer.parseInt(task_reminder.get(i)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });

        // Get Categories SQLite into Realtime Database.
        ArrayList<String> category_id = new ArrayList<>();
        ArrayList<String> category_id_ = new ArrayList<>();
        ArrayList<String> category_name = new ArrayList<>();
        ArrayList<String> category_color_Ref = new ArrayList<>();
        ArrayList<String> category_deleted = new ArrayList<>();

        referenceCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RealtimeCategory categories = dataSnapshot.getValue(RealtimeCategory.class);
                    category_id.add(categories.get_id());
                    category_id_.add(categories.get_id_());
                    category_name.add(categories.getCategory_name());
                    category_color_Ref.add(categories.getCategory_color_Ref());
                    category_deleted.add(categories.getCategory_deleted());
                }
                // Store data in local database
                Toast.makeText(context, "category : " + String.valueOf(category_id.size()), Toast.LENGTH_SHORT).show();
                MyDatabaseHelper_category myDB_category = new MyDatabaseHelper_category(context);
                myDB_category.deleteAllData();
                for (int i = 0; i < category_id.size(); i++) {
                    myDB_category.addCategory2(category_id_.get(i), category_name.get(i), Integer.parseInt(category_color_Ref.get(i)), category_deleted.get(i));
                }

                // Modifie les ids pour que les donne de Database SQL sois la meme que Firebase
                for (int i = 0; i < category_id.size(); i++) {
                    //myDB_category.updateData(category_id.get(i), category_name.get(i), Integer.parseInt(category_color_Ref.get(i)), category_deleted.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });

        // Get Notes SQLite into Realtime Database.
        ArrayList<String> note_id = new ArrayList<>();
        ArrayList<String> note_title = new ArrayList<>();
        ArrayList<String> note_description = new ArrayList<>();
        ArrayList<String> note_time = new ArrayList<>();

        referenceNotes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RealtimeNotes note = dataSnapshot.getValue(RealtimeNotes.class);
                    note_id.add(note.get_id());
                    note_title.add(note.getNote_title());
                    note_description.add(note.getNote_description());
                    note_time.add(note.getNote_time());
                }
                Toast.makeText(context, "notes : " + String.valueOf(note_id.size()), Toast.LENGTH_SHORT).show();
                // Store data in local database
                MyDatabaseHelper_notes myDB_notes = new MyDatabaseHelper_notes(context);
                myDB_notes.deleteAllData();
                for (int i = 0; i < note_id.size(); i++) {
                    myDB_notes.addBook(note_title.get(i), note_description.get(i), note_time.get(i));
                }
                // Modifie les ids pour que les donne de Database SQL sois la meme que Firebase
                for (int i = 0; i < note_id.size(); i++) {
                    //myDB_notes.updateData(note_id.get(i), note_title.get(i), note_description.get(i), note_time.get(i));
                }
                progressDialog.dismiss();
                Toast.makeText(context, String.valueOf(task_id.size())+  " + " + String.valueOf(category_id.size()) + " + " + String.valueOf(note_id.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setAllDataIntoRealtimeDatabase() {

        // Set Tasks SQLite into Realtime Database.
        ArrayList<String> task_id = new ArrayList<>();
        ArrayList<String> task_date = new ArrayList<>();
        ArrayList<String> task_title = new ArrayList<>();
        ArrayList<String> task_description = new ArrayList<>();
        ArrayList<String> task_priority = new ArrayList<>();
        ArrayList<String> task_category = new ArrayList<>();
        ArrayList<String> task_time = new ArrayList<>();
        ArrayList<String> task_done = new ArrayList<>();
        ArrayList<String> task_reminder = new ArrayList<>();

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
                task_done.add(taskCursor.getString(7));
                task_reminder.add(taskCursor.getString(8));
            }
        }

        for (int i = 0; i < task_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", task_id.get(i));
            hashMap.put("task_date", task_date.get(i));
            hashMap.put("task_title", task_title.get(i));
            hashMap.put("task_description", task_description.get(i));
            hashMap.put("task_priority", task_priority.get(i));
            hashMap.put("task_category", task_category.get(i));
            hashMap.put("task_time", task_time.get(i));
            hashMap.put("task_done", task_done.get(i));
            hashMap.put("task_reminder", task_reminder.get(i));

            referenceTasks.child("task : " + String.valueOf(i)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set Categories SQLite into Realtime Database.
        ArrayList<String> category_id = new ArrayList<>();
        ArrayList<String> category_id_ = new ArrayList<>();
        ArrayList<String> category_name = new ArrayList<>();
        ArrayList<String> category_color_Ref = new ArrayList<>();
        ArrayList<String> category_deleted = new ArrayList<>();

        MyDatabaseHelper_category myDB_category = new MyDatabaseHelper_category(context);
        Cursor categoryCursor = myDB_category.readAllData();
        if (categoryCursor.getCount() == 0) {
            // Empty.
        } else {
            while (categoryCursor.moveToNext()) {
                category_id.add(categoryCursor.getString(0));
                category_id_.add(categoryCursor.getString(1));
                category_name.add(categoryCursor.getString(2));
                category_color_Ref.add(categoryCursor.getString(3));
                category_deleted.add(categoryCursor.getString(4));
            }
        }

        for (int i = 0; i < category_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", category_id.get(i));
            hashMap.put("_id_", category_id_.get(i));
            hashMap.put("category_name", category_name.get(i));
            hashMap.put("category_color_Ref", category_color_Ref.get(i));
            hashMap.put("category_deleted", category_deleted.get(i));

            referenceCategories.child("category : " + String.valueOf(i)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set Notes SQLite into Realtime Database.
        ArrayList<String> note_id = new ArrayList<>();
        ArrayList<String> note_title = new ArrayList<>();
        ArrayList<String> note_description = new ArrayList<>();
        ArrayList<String> note_time = new ArrayList<>();

        MyDatabaseHelper_notes myDB_notes = new MyDatabaseHelper_notes(context);
        Cursor noteCursor = myDB_notes.readAllData();
        if (noteCursor.getCount() == 0) {
            // Empty.
        } else {
            while (noteCursor.moveToNext()) {
                note_id.add(noteCursor.getString(0));
                note_title.add(noteCursor.getString(1));
                note_description.add(noteCursor.getString(2));
                note_time.add(noteCursor.getString(3));
            }
        }

        for (int i = 0; i < note_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", note_id.get(i));
            hashMap.put("note_title", note_title.get(i));
            hashMap.put("note_description", note_description.get(i));
            hashMap.put("note_time", note_time.get(i));

            referenceNotes.child("note : " + String.valueOf(i)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}