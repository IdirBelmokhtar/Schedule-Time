package com.ecommerce.scheduletime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ecommerce.scheduletime.Adapter.RecyclerViewTasksAdapter;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    String fUserID;

    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fUserID = "fst_user";

        }

    public void button2(View view) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Schedule Time/user").child(fUserID);

        // Set first (Task/Category/Note) in Realtime Database.

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat KK = new SimpleDateFormat("KK");
        SimpleDateFormat HH = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat aa = new SimpleDateFormat("aa");

        int year_, month_, dayOfMonth_;

        year_ = calendar.get(Calendar.YEAR);
        month_ = calendar.get(Calendar.MONTH);
        dayOfMonth_ = calendar.get(Calendar.DAY_OF_MONTH);

        String time_ = String.valueOf(calendar.get(Calendar.YEAR)) + // calendar.getTime().getYear() return year = 122 not 2022!
                "-" + String.valueOf(calendar.getTime().getMonth()) +
                "-" + String.valueOf(calendar.getTime().getDate()) +
                "-" + String.valueOf(calendar.getTime().getHours()) +
                "-" + String.valueOf(calendar.getTime().getMinutes());

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

        HashMap<String, String> hashMap2 = new HashMap<>();
        hashMap2.put("_id", "1");
        hashMap2.put("_id_", "1");
        hashMap2.put("category_name", "Work");
        hashMap2.put("category_color_Ref", String.valueOf(R.color.colorViewThree));
        hashMap2.put("category_deleted", "no");

        HashMap<String, String> hashMap3 = new HashMap<>();
        hashMap3.put("_id", "1");
        hashMap3.put("_id_", "1");
        hashMap3.put("note_title", getResources().getString(R.string.hello));
        hashMap3.put("note_description", getResources().getString(R.string.description_of_our_message_to_users));
        hashMap3.put("note_time", time_);

        usersRef.child("Tasks").child("task : 0").setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        usersRef.child("Categories").child("category : 0").setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        usersRef.child("Notes").child("note : 0").setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}