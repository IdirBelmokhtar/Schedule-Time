package com.ecommerce.scheduletime.Sync;

import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getCategories;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getNotes;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getTasks;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecommerce.scheduletime.Model.Firebase.RealtimeCategory;
import com.ecommerce.scheduletime.Model.Firebase.RealtimeNotes;
import com.ecommerce.scheduletime.Model.Firebase.RealtimeTasks;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_notes;
import com.ecommerce.scheduletime.SQLite.NewCategory;
import com.ecommerce.scheduletime.SQLite.NewNotes;
import com.ecommerce.scheduletime.SQLite.NewTasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SyncDataBaseServiceUpdate extends IntentService {

    Context context;

    String fUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Schedule Time/Users").child(fUserID);

    NewTasks newTasks;
    ArrayList<String> task_id = new ArrayList<>();
    ArrayList<String> task_id_ = new ArrayList<>();
    ArrayList<String> task_date = new ArrayList<>();
    ArrayList<String> task_title = new ArrayList<>();
    ArrayList<String> task_description = new ArrayList<>();
    ArrayList<String> task_priority = new ArrayList<>();
    ArrayList<String> task_category = new ArrayList<>();
    ArrayList<String> task_time = new ArrayList<>();
    ArrayList<String> task_done = new ArrayList<>();
    ArrayList<String> task_reminder = new ArrayList<>();
    ArrayList<String> task_condition = new ArrayList<>();

    NewCategory newCategory;
    ArrayList<String> category_id = new ArrayList<>();
    ArrayList<String> category_id_ = new ArrayList<>();
    ArrayList<String> category_name = new ArrayList<>();
    ArrayList<String> category_color_Ref = new ArrayList<>();
    ArrayList<String> category_deleted = new ArrayList<>();
    ArrayList<String> category_condition = new ArrayList<>();

    NewNotes newNotes;
    ArrayList<String> note_id = new ArrayList<>();
    ArrayList<String> note_id_ = new ArrayList<>();
    ArrayList<String> note_title = new ArrayList<>();
    ArrayList<String> note_description = new ArrayList<>();
    ArrayList<String> note_time = new ArrayList<>();
    ArrayList<String> note_condition = new ArrayList<>();

    public SyncDataBaseServiceUpdate() {
        super("SyncDataBaseServiceUpdate");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This is where you put your background work, such as network requests or database access.
        // This method is executed on a separate thread.

        /** Check the user is all ready connected or not*/
        SharedPreferences prefs = context.getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE);
        boolean change = prefs.getBoolean("change", false);
        if (change) {
            // Get Updates of Tasks.
            newTasks = new NewTasks(context);
            Cursor cursorTasks = newTasks.readAllData();
            while (cursorTasks.moveToNext()) {
                task_id.add(cursorTasks.getString(0));
                task_id_.add(cursorTasks.getString(1));
                task_date.add(cursorTasks.getString(2));
                task_title.add(cursorTasks.getString(3));
                task_description.add(cursorTasks.getString(4));
                task_priority.add(cursorTasks.getString(5));
                task_category.add(cursorTasks.getString(6));
                task_time.add(cursorTasks.getString(7));
                task_done.add(cursorTasks.getString(8));
                task_reminder.add(cursorTasks.getString(9));
                task_condition.add(cursorTasks.getString(10));
            }

            tasksThatWillSend();

            // Get Updates of category
            newCategory = new NewCategory(context);
            Cursor cursorCategory = newCategory.readAllData();
            while (cursorCategory.moveToNext()) {
                category_id.add(cursorCategory.getString(0));
                category_id_.add(cursorCategory.getString(1));
                category_name.add(cursorCategory.getString(2));
                category_color_Ref.add(cursorCategory.getString(3));
                category_deleted.add(cursorCategory.getString(4));
                category_condition.add(cursorCategory.getString(5));
            }

            categoryThatWillSend();

            // Get Updates of notes
            newNotes = new NewNotes(context);
            Cursor cursorNotes = newNotes.readAllData();
            while (cursorNotes.moveToNext()) {
                note_id.add(cursorNotes.getString(0));
                note_id_.add(cursorNotes.getString(1));
                note_title.add(cursorNotes.getString(2));
                note_description.add(cursorNotes.getString(3));
                note_time.add(cursorNotes.getString(4));
                note_condition.add(cursorNotes.getString(5));
            }

            notesThatWillSend();


            /** Start sending data to Firebase Realtime Database and delete it from [ {@link NewTasks}, {@link NewCategory}, {@link NewNotes} ]*/
            sendDataToRealtimeDatabase();
        }
    }

    private void tasksThatWillSend() {
        /** Delete rows that no need from DB ({@link NewTasks}) by checking for duplicate ID_, and keep the last ID_ row.*/
        for (int i = task_id_.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (task_id_.get(i).equals(task_id_.get(j))) {
                    newTasks.deleteOneRow(task_id.get(j));
                }
            }
        }

        task_id.clear();
        task_id_.clear();
        task_date.clear();
        task_title.clear();
        task_description.clear();
        task_priority.clear();
        task_category.clear();
        task_time.clear();
        task_done.clear();
        task_reminder.clear();
        task_condition.clear();

        // Get Updates of tasks.
        Cursor cursorTasks = newTasks.readAllData();
        while (cursorTasks.moveToNext()) {
            task_id.add(cursorTasks.getString(0));
            task_id_.add(cursorTasks.getString(1));
            task_date.add(cursorTasks.getString(2));
            task_title.add(cursorTasks.getString(3));
            task_description.add(cursorTasks.getString(4));
            task_priority.add(cursorTasks.getString(5));
            task_category.add(cursorTasks.getString(6));
            task_time.add(cursorTasks.getString(7));
            task_done.add(cursorTasks.getString(8));
            task_reminder.add(cursorTasks.getString(9));
            task_condition.add(cursorTasks.getString(10));
        }
    }

    private void categoryThatWillSend() {
        /** Delete rows that no need from DB ({@link NewCategory}) by checking for duplicate ID_, and keep the last ID_ row.*/
        for (int i = category_id_.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (category_id_.get(i).equals(category_id_.get(j))) {
                    newCategory.deleteOneRow(category_id.get(j));
                }
            }
        }

        category_id.clear();
        category_id_.clear();
        category_name.clear();
        category_color_Ref.clear();
        category_deleted.clear();
        category_condition.clear();

        // Get Updates of category.
        Cursor cursorCategory = newCategory.readAllData();
        while (cursorCategory.moveToNext()) {
            category_id.add(cursorCategory.getString(0));
            category_id_.add(cursorCategory.getString(1));
            category_name.add(cursorCategory.getString(2));
            category_color_Ref.add(cursorCategory.getString(3));
            category_deleted.add(cursorCategory.getString(4));
            category_condition.add(cursorCategory.getString(5));
        }
    }

    private void notesThatWillSend() {
        /** Delete rows that no need from DB ({@link NewNotes}) by checking for duplicate ID_, and keep the last ID_ row.*/
        for (int i = note_id_.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (note_id_.get(i).equals(note_id_.get(j))) {
                    newNotes.deleteOneRow(note_id.get(j));
                }
            }
        }

        note_id.clear();
        note_id_.clear();
        note_title.clear();
        note_description.clear();
        note_time.clear();
        note_condition.clear();

        // Get Updates of notes.
        Cursor cursorNotes = newNotes.readAllData();
        while (cursorNotes.moveToNext()) {
            note_id.add(cursorNotes.getString(0));
            note_id_.add(cursorNotes.getString(1));
            note_title.add(cursorNotes.getString(2));
            note_description.add(cursorNotes.getString(3));
            note_time.add(cursorNotes.getString(4));
            note_condition.add(cursorNotes.getString(5));
        }
    }

    private void sendDataToRealtimeDatabase() {
        for (int i = 0; i < task_id.size(); i++) {

            DatabaseReference ref = usersRef.child("Tasks").child("task : " + task_id_.get(i));

            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("_id", task_id.get(i));
            hashMap1.put("_id_", task_id_.get(i));
            hashMap1.put("task_date", task_date.get(i));
            hashMap1.put("task_title", task_title.get(i));
            hashMap1.put("task_description", task_description.get(i));
            hashMap1.put("task_priority", task_priority.get(i));
            hashMap1.put("task_category", task_category.get(i));
            hashMap1.put("task_time", task_time.get(i));
            hashMap1.put("task_done", task_done.get(i));
            hashMap1.put("task_reminder", task_reminder.get(i));

            if (task_condition.get(i).equals("insert")) {
                int finalI = i;
                ref.setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newTasks.deleteOneRow(task_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (task_condition.get(i).equals("update")) {
                int finalI = i;
                ref.updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newTasks.deleteOneRow(task_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (task_condition.get(i).equals("delete")) {
                int finalI1 = i;
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newTasks.deleteOneRow(task_id.get(finalI1));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newTasks.deleteOneRow(task_id.get(finalI1));
                    }
                });
            }
        }

        for (int i = 0; i < category_id.size(); i++) {

            DatabaseReference ref = usersRef.child("Categories").child("category : " + category_id_.get(i));

            HashMap<String, Object> hashMap2 = new HashMap<>();
            hashMap2.put("_id", category_id.get(i));
            hashMap2.put("_id_", category_id_.get(i));
            hashMap2.put("category_name", category_name.get(i));
            hashMap2.put("category_color_Ref", category_color_Ref.get(i));
            hashMap2.put("category_deleted", category_deleted.get(i));

            if (category_condition.get(i).equals("insert")) {
                int finalI = i;
                ref.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newCategory.deleteOneRow(category_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (category_condition.get(i).equals("update")) {
                int finalI = i;
                ref.updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newCategory.deleteOneRow(category_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (category_condition.get(i).equals("delete")) {
                int finalI1 = i;
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newCategory.deleteOneRow(category_id.get(finalI1));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                        newCategory.deleteOneRow(category_id.get(finalI1));
                    }
                });
            }
        }

        for (int i = 0; i < note_id.size(); i++) {

            DatabaseReference ref = usersRef.child("Notes").child("note : " + note_id_.get(i));

            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("_id", note_id.get(i));
            hashMap3.put("_id_", note_id_.get(i));
            hashMap3.put("note_title", note_title.get(i));
            hashMap3.put("note_description", note_description.get(i));
            hashMap3.put("note_time", note_time.get(i));

            if (note_condition.get(i).equals("insert")) {
                int finalI = i;
                ref.setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newNotes.deleteOneRow(note_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (note_condition.get(i).equals("update")) {
                int finalI = i;
                ref.updateChildren(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newNotes.deleteOneRow(note_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (note_condition.get(i).equals("delete")) {
                int finalI = i;
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        newNotes.deleteOneRow(note_id.get(finalI));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                        newNotes.deleteOneRow(note_id.get(finalI));
                    }
                });
            }
        }
    }
}
