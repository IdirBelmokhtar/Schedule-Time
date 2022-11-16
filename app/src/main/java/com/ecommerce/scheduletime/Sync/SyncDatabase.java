package com.ecommerce.scheduletime.Sync;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncDatabase {

    Context context;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
    DatabaseReference referenceTasks = referenceUser.child("Tasks");
    DatabaseReference referenceCategories = referenceUser.child("Categories");
    DatabaseReference referenceNotes = referenceUser.child("Notes");

    public SyncDatabase (@Nullable Context context){
        this.context = context;
        setAllDataIntoRealtimeDatabase();
    }

    void setAllDataIntoRealtimeDatabase(){

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
                category_name.add(categoryCursor.getString(1));
                category_color_Ref.add(categoryCursor.getString(2));
                category_deleted.add(categoryCursor.getString(3));
            }
        }

        for (int i = 0; i < category_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", category_id.get(i));
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