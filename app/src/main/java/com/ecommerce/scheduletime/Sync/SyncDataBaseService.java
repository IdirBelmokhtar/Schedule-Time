package com.ecommerce.scheduletime.Sync;

import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getTasks;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getCategories;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getNotes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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

public class SyncDataBaseService extends IntentService {

    Context context;
    boolean done1 = false, done2 = false, done3 = false;

    String fUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Schedule Time/Users").child(fUserID);

    DatabaseReference referenceTasks = usersRef.child("Tasks");
    DatabaseReference referenceCategories = usersRef.child("Categories");
    DatabaseReference referenceNotes = usersRef.child("Notes");

    public SyncDataBaseService() {
        super("SyncDataBaseService");
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

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the fUserID exists in the database
                if (dataSnapshot.exists()) {
                    //setAllDataIntoRealtimeDatabase();
                    getAllDataSQLiteDatabase();
                } else {
                    // Add user and first (tasks/category/note) in Realtime Database Firebase.
                    createFirstDataInRealtimeDatabase();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value : " + fUserID + " / " + String.valueOf(error));
            }
        });
    }

    public void getAllDataSQLiteDatabase() {
        System.out.println("start retrieve data from firebase");

        // Get Tasks Realtime Database into SQLite.
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

        referenceTasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RealtimeTasks task = dataSnapshot.getValue(RealtimeTasks.class);
                    task_id.add(task.get_id());
                    task_id_.add(task.get_id_());
                    task_date.add(task.getTask_date());
                    task_title.add(task.getTask_title());
                    task_description.add(task.getTask_description());
                    task_priority.add(task.getTask_priority());
                    task_category.add(task.getTask_category());
                    task_time.add(task.getTask_time());
                    task_done.add(task.getTask_done());
                    task_reminder.add(task.getTask_reminder());
                }
                // Store data in local database
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                NewTasks newTasks = new NewTasks(context);
                myDB.deleteAllData();
                newTasks.deleteAllData();
                for (int i = 0; i < task_id.size(); i++) {
                    myDB.addSchedule2(task_id_.get(i), task_date.get(i), task_title.get(i), task_description.get(i), task_priority.get(i),
                            task_category.get(i), task_time.get(i), task_done.get(i), Integer.parseInt(task_reminder.get(i)));
                }
                getTasks = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("onCancelled tasks : " + String.valueOf(error));
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
                MyDatabaseHelper_category myDB_category = new MyDatabaseHelper_category(context);
                NewCategory newCategory = new NewCategory(context);
                myDB_category.deleteAllData();
                newCategory.deleteAllData();
                for (int i = 0; i < category_id.size(); i++) {
                    myDB_category.addCategory2(category_id_.get(i), category_name.get(i), Integer.parseInt(category_color_Ref.get(i)), category_deleted.get(i));
                }
                getCategories = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("onCancelled category : " + String.valueOf(error));
            }
        });

        // Get Notes SQLite into Realtime Database.
        ArrayList<String> note_id = new ArrayList<>();
        ArrayList<String> note_id_ = new ArrayList<>();
        ArrayList<String> note_title = new ArrayList<>();
        ArrayList<String> note_description = new ArrayList<>();
        ArrayList<String> note_time = new ArrayList<>();

        referenceNotes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RealtimeNotes note = dataSnapshot.getValue(RealtimeNotes.class);
                    note_id.add(note.get_id());
                    note_id_.add(note.get_id_());
                    note_title.add(note.getNote_title());
                    note_description.add(note.getNote_description());
                    note_time.add(note.getNote_time());
                }
                // Store data in local database
                MyDatabaseHelper_notes myDB_notes = new MyDatabaseHelper_notes(context);
                NewNotes newNotes = new NewNotes(context);
                myDB_notes.deleteAllData();
                newNotes.deleteAllData();
                for (int i = 0; i < note_id.size(); i++) {
                    myDB_notes.addBook2(note_id_.get(i), note_title.get(i), note_description.get(i), note_time.get(i));
                }
                getNotes = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("onCancelled notes : " + String.valueOf(error));
            }
        });


    }

    public void createFirstDataInRealtimeDatabase() {
        /** In Future add also Holidays*/

        // Set firstTask in Realtime Database.
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        int year_, month_, dayOfMonth_;

        year_ = calendar.get(Calendar.YEAR);
        month_ = calendar.get(Calendar.MONTH);
        dayOfMonth_ = calendar.get(Calendar.DAY_OF_MONTH);

        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("_id", "1");
        hashMap1.put("_id_", "1");
        hashMap1.put("task_date", String.valueOf(year_) + "-" + String.valueOf(month_) + "-" + String.valueOf(dayOfMonth_));
        hashMap1.put("task_title", getResources().getString(R.string.hello));
        hashMap1.put("task_description", getResources().getString(R.string.description_of_our_message_to_users));
        hashMap1.put("task_priority", "default");
        hashMap1.put("task_category", "[1]");
        hashMap1.put("task_time", String.valueOf(HH.format(calendar.getTime())) + ":" + String.valueOf(mm.format(calendar.getTime())));
        hashMap1.put("task_done", "no");
        hashMap1.put("task_reminder", "0");

        usersRef.child("Tasks").child("task : 1").setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                done1 = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });


        // Set Categories SQLite into Realtime Database.
        HashMap<String, String> hashMap2 = new HashMap<>();
        hashMap2.put("_id", "1");
        hashMap2.put("_id_", "1");
        hashMap2.put("category_name", "Work");
        hashMap2.put("category_color_Ref", String.valueOf(R.color.colorViewThree));
        hashMap2.put("category_deleted", "no");

        usersRef.child("Categories").child("category : 1").setValue(hashMap2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        done2 = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });


        // Set Notes SQLite into Realtime Database
        String time_ = String.valueOf(calendar.get(Calendar.YEAR)) + // calendar.getTime().getYear() return year = 122 not 2022!
                "-" + String.valueOf(calendar.getTime().getMonth()) +
                "-" + String.valueOf(calendar.getTime().getDate()) +
                "-" + String.valueOf(calendar.getTime().getHours()) +
                "-" + String.valueOf(calendar.getTime().getMinutes());

        HashMap<String, String> hashMap3 = new HashMap<>();
        hashMap3.put("_id", "1");
        hashMap3.put("_id_", "1");
        hashMap3.put("note_title", getResources().getString(R.string.hello));
        hashMap3.put("note_description", getResources().getString(R.string.description_of_our_message_to_users));
        hashMap3.put("note_time", time_);

        usersRef.child("Notes").child("note : 1").setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                done3 = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
        next_();
    }

    private void next_() {
        if (done1 && done2 && done3){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllDataSQLiteDatabase();
                }
            }, 1000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    next_();
                }
            }, 1000);
        }
    }

    public void setAllDataIntoRealtimeDatabase() {
        // -- Testing --

        // Set Tasks SQLite into Realtime Database.
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

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor taskCursor = myDB.readAllData();
        if (taskCursor.getCount() == 0) {
            // Empty.
        } else {
            while (taskCursor.moveToNext()) {
                task_id.add(taskCursor.getString(0));
                task_id_.add(taskCursor.getString(1));
                task_date.add(taskCursor.getString(2));
                task_title.add(taskCursor.getString(3));
                task_description.add(taskCursor.getString(4));
                task_priority.add(taskCursor.getString(5));
                task_category.add(taskCursor.getString(6));
                task_time.add(taskCursor.getString(7));
                task_done.add(taskCursor.getString(8));
                task_reminder.add(taskCursor.getString(9));
            }
        }

        for (int i = 0; i < task_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", task_id.get(i));
            hashMap.put("_id_", task_id_.get(i));
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
                    //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
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

            referenceCategories.child("category : " + String.valueOf(i)).setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Set Notes SQLite into Realtime Database.
        ArrayList<String> note_id = new ArrayList<>();
        ArrayList<String> note_id_ = new ArrayList<>();
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
                note_id_.add(noteCursor.getString(1));
                note_title.add(noteCursor.getString(2));
                note_description.add(noteCursor.getString(3));
                note_time.add(noteCursor.getString(4));
            }
        }

        for (int i = 0; i < note_id.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("_id", note_id.get(i));
            hashMap.put("_id_", note_id_.get(i));
            hashMap.put("note_title", note_title.get(i));
            hashMap.put("note_description", note_description.get(i));
            hashMap.put("note_time", note_time.get(i));

            referenceNotes.child("note : " + String.valueOf(i)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
